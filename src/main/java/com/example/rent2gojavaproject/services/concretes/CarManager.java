package com.example.rent2gojavaproject.services.concretes;

import com.example.rent2gojavaproject.core.exceptions.NotFoundException;
import com.example.rent2gojavaproject.core.utilities.alerts.Message;
import com.example.rent2gojavaproject.core.utilities.mappers.ModelMapperService;
import com.example.rent2gojavaproject.core.utilities.results.DataResult;
import com.example.rent2gojavaproject.core.utilities.results.Result;
import com.example.rent2gojavaproject.core.utilities.results.SuccessDataResult;
import com.example.rent2gojavaproject.core.utilities.results.SuccessResult;
import com.example.rent2gojavaproject.models.Car;
import com.example.rent2gojavaproject.repositories.CarRepository;
import com.example.rent2gojavaproject.services.abstracts.CarService;
import com.example.rent2gojavaproject.services.abstracts.FileUpload;
import com.example.rent2gojavaproject.services.dtos.requests.carRequest.AddCarRequest;
import com.example.rent2gojavaproject.services.dtos.requests.carRequest.UpdateCarRequest;
import com.example.rent2gojavaproject.services.dtos.responses.carResponse.GetCarListResponse;
import com.example.rent2gojavaproject.services.dtos.responses.carResponse.GetCarResponse;
import com.example.rent2gojavaproject.services.rules.CarBusinessRules;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;

    private ModelMapperService mapperService;

    private CarBusinessRules businessRules;

    private EntityManager entityManager;

    private FileUpload  fileUpload;


    @Override
    public DataResult<List<GetCarListResponse>> getAllCars() {

        List<Car> cars = this.carRepository.findAll();
        List<GetCarListResponse> responses = cars.stream().map(car -> this.mapperService
                        .forResponse()
                        .map(car, GetCarListResponse.class))
                .collect(Collectors.toList());


        return new SuccessDataResult<>(responses, Message.GET_ALL.getMessage());
    }

    @Override
    public DataResult<Iterable<GetCarListResponse>> findAll(boolean isActive) {

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("isActiveFilterCar");

        filter.setParameter("isActive", isActive);

        Iterable<GetCarListResponse> cars = this.carRepository.findAll()
                .stream().map(car -> this.mapperService.forResponse()
                        .map(car, GetCarListResponse.class))
                .collect(Collectors.toList());
        session.disableFilter("isActiveFilterCar");

        return new SuccessDataResult<>(cars, Message.GET_ALL.getMessage());
    }

    @Override
    public DataResult<GetCarResponse> getById(int id) {

        Car car = this.carRepository.findById(id).orElseThrow(() -> new NotFoundException("Couldn't find car id : " + id));
        GetCarResponse response = this.mapperService.forResponse().map(car, GetCarResponse.class);

        return new SuccessDataResult<>(response, Message.GET.getMessage());
    }

    @Override
    public Result addCar(AddCarRequest addCarRequest, MultipartFile file) throws IOException {

        String imageUrl= fileUpload.uploadFile(file,addCarRequest.getPlate());

        String editPlate = this.businessRules.plateUniqueness(addCarRequest.getPlate());
        addCarRequest.setPlate(editPlate);

        this.businessRules.updateCarMethod(addCarRequest.getModelId(), addCarRequest.getColorId());

        Car car = this.mapperService.forRequest().map(addCarRequest, Car.class);
        car.setImageUrl(imageUrl);
        this.carRepository.save(car);

        return new SuccessResult(Message.ADD.getMessage());

    }


    @Override
    public Result updateCar(UpdateCarRequest updateCarRequest) {

        String editPlate = this.businessRules.plateUniqueness(updateCarRequest.getPlate());
        updateCarRequest.setPlate(editPlate);
        this.businessRules.updateCarMethod(updateCarRequest.getModelId(), updateCarRequest.getColorId());
        this.carRepository.findById(updateCarRequest.getId()).orElseThrow(() -> new NotFoundException("Car not found"));

        Car car = this.mapperService.forRequest().map(updateCarRequest, Car.class);
        this.carRepository.save(car);

        return new SuccessResult(Message.UPDATE.getMessage());
    }

    @Override
    public Result deleteCar(int id) {

        Car car = this.carRepository.findById(id).orElseThrow(() -> new NotFoundException("id not found : " + id));
        car.setDeletedAt(LocalDate.now());

        this.carRepository.deleteById(id);

        return new SuccessResult(Message.DELETE.getMessage());
    }

    @Override
    public boolean existsByPlate(String plate) {
        return this.carRepository.existsByPlate(plate);
    }

    @Override
    public boolean existsById(int id) {
        return this.carRepository.existsById(id);
    }
}
