package com.example.rent2gojavaproject.services.rules;

import com.example.rent2gojavaproject.core.exceptions.BusinessRuleException;
import com.example.rent2gojavaproject.core.exceptions.NotFoundException;
import com.example.rent2gojavaproject.models.Discount;
import com.example.rent2gojavaproject.services.abstracts.CarService;
import com.example.rent2gojavaproject.services.abstracts.CustomerService;
import com.example.rent2gojavaproject.services.abstracts.DiscountService;
import com.example.rent2gojavaproject.services.abstracts.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@AllArgsConstructor
@Service
public class RentalBusinessRules {

    private CarService carService;
    private CustomerService customerService;
    private EmployeeService employeeService;
    private DiscountService discountService;

    public void checkIfExistsById(int carId, int customerId, int employeeId) {

        if (!(carService.existsById(carId))) {
            throw new NotFoundException("Car ID doesn't exist !");
        } else if (!(customerService.existsById(customerId))) {
            throw new NotFoundException("Customer ID doesn't exist !");
        } else if (!(employeeService.existsById(employeeId))) {
            throw new NotFoundException("Employee ID doesn't exist !");
        }
    }

    public void checkIfKilometer(int kilometer, Integer endKilometer) {

        Integer newKilometer = Integer.valueOf(kilometer);
        if (newKilometer > endKilometer) {
            throw new BusinessRuleException("The last kilometer of the vehicle cannot be lower than the delivered mileage.");
        }
    }

    public void checkRentalPeriod(LocalDate startDate, LocalDate endDate) {

        Period period = Period.between(startDate, endDate);
        int rentalDays = period.getDays();
        if (startDate.isAfter(endDate)) {
            throw new BusinessRuleException("Start date must be before rental end date");
        } else if (rentalDays > 25) {
            throw new BusinessRuleException("Car can be rented for a maximum of 25 days.!");
        }
    }

    public Discount getDiscountByCodeOrDefault(String discountCode) {

        if (discountCode == null || discountCode.isEmpty()) {
            return discountService.findByDiscountCode("DEFAULT");
        }

        Discount selectedDiscount = discountService.findByDiscountCode(discountCode);

        return (selectedDiscount != null) ? selectedDiscount : discountService.findByDiscountCode("DEFAULT");
    }

    public double calculateTotalPrice(LocalDate startDate, LocalDate endDate, double dailyPrice, String discountCode) {

        Discount discount = getDiscountByCodeOrDefault(discountCode);

        double totalDiscount = (discount.getPercentage() / 100) * dailyPrice * (endDate.toEpochDay() - startDate.toEpochDay());
        double totalPrice = dailyPrice * (endDate.toEpochDay() - startDate.toEpochDay()) - totalDiscount;

        return (totalPrice < 0) ? 0 : totalPrice;
    }

}
