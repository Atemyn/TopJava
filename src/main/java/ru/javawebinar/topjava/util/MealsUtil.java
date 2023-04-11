package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealWithExcess> mealsTo =
                filteredByCycles(meals, LocalTime.of(7, 0),
                        LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<MealWithExcess> filteredByCycles(List<Meal> meals,
                                                        LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<MealWithExcess> mealWithExcesses = new ArrayList<>();

        for (var meal : meals) {
            if (TimeUtil.isBetweenInclusive(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealWithExcesses
                        .add(new MealWithExcess(meal.getDateTime(),
                                meal.getDescription(), meal.getCalories(),
                                sumCaloriesForDate(meals, meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }

        return mealWithExcesses;
    }

    private static int sumCaloriesForDate(List<Meal> meals, LocalDate date) {
        int sum = 0;
        for (var meal : meals) {
            if (meal.getDateTime().toLocalDate().isEqual(date)) {
                sum += meal.getCalories();
            }
        }
        return sum;
    }

    public static List<MealWithExcess> filteredByStreams(List<Meal> meals,
                                                         LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumForDate = meals.stream().collect(Collectors.groupingBy(
                Meal::getDate, Collectors.summingInt(Meal::getCalories)));


        return meals.stream()
                .filter(um -> TimeUtil.isBetweenInclusive(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new MealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                        caloriesSumForDate.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .toList();
    }
}
