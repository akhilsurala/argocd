package com.sunseed.helper;

import com.sunseed.entity.Bed;
import com.sunseed.entity.CropBedSection;
import com.sunseed.projection.Months;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostprocessingHelper {

    @Value("${crop.intervalType}")
    private int intervalType;


    // calculate interval
//    public static List<Integer> calculateIntervalsForCrop(LocalDate startDate, int durationInDays ) {
//        List<Integer> intervalNumbers = new ArrayList<>();
//        LocalDate endDate = startDate.plusDays(durationInDays);
//        LocalDate currentDate = startDate;
//        int intervalIncrement = 7; // Default to weekly
//
//        switch (intervalType.toLowerCase()) {
//            case "biweekly":
//                intervalIncrement = 14;
//                break;
//            case "monthly":
//                intervalIncrement = 30; // Approximate monthly increment
//                break;
//            case "weekly":
//            default:
//                intervalIncrement = 7;
//                break;
//        }
//
//        while (!currentDate.isAfter(endDate)) {
//            // Calculate interval number based on January 1 as the start of interval 1
//            int intervalNumber = (int) ChronoUnit.DAYS.between(LocalDate.of(currentDate.getYear(), 1, 1), currentDate) / intervalIncrement + 1;
//            intervalNumbers.add(intervalNumber);
//
//            // Move to the next interval
//            currentDate = currentDate.plusDays(intervalIncrement);
//        }
//
//        return intervalNumbers;
//    }
//    public List<Integer> calculateIntervalsForCrop(LocalDate startDate, int durationInDays) {
//        List<Integer> intervalNumbers = new ArrayList<>();
//        LocalDate endDate = startDate.plusDays(durationInDays);
//        LocalDate currentDate = startDate;
//        System.out.println("static interval type :" + intervalType);
//        while (!currentDate.isAfter(endDate)) {
//            // Calculate interval number based on January 1 as the start of interval 1
//            int intervalNumber = (int) ChronoUnit.DAYS.between(
//                    LocalDate.of(currentDate.getYear(), 1, 1), currentDate
//            ) / intervalType + 1;
//
//            intervalNumbers.add(intervalNumber);
//
//            // Move to the next interval based on intervalType days
//            currentDate = currentDate.plusDays(intervalType);
//        }
//        Collections.sort(intervalNumbers);
//        return intervalNumbers;
//    }
//
//    // interval when only start date is given
//    public  int calculateIntervalNumberForDate(LocalDate startDate) {
//        // Calculate interval number based on January 1 as the start of interval 1
//        int intervalNumber = (int) ChronoUnit.DAYS.between(
//                LocalDate.of(startDate.getYear(), 1, 1), startDate
//        ) / intervalType + 1;
//
//        return intervalNumber;
//    }

    // Calculate intervals for the given duration, using a fixed non-leap year (2023)
    public List<Integer> calculateIntervalsForCrop(LocalDate startDate, int durationInDays) {
        List<Integer> intervalNumbers = new ArrayList<>();
        LocalDate endDate = startDate.plusDays(durationInDays);
        LocalDate currentDate = startDate;
        LocalDate baseYearStart = LocalDate.of(2023, 1, 1);  // Fixed base year

        while (!currentDate.isAfter(endDate)) {
            // Calculate interval number using the fixed base year
            int intervalNumber = (int) ChronoUnit.DAYS.between(
                    baseYearStart, currentDate.withYear(2023)
            ) / intervalType + 1;

//            // Snap interval number to valid range
//            intervalNumber = Math.min(intervalNumber, getMaxNumberOfBlocks(365, intervalType));
//
//            // Avoid duplicates
//            if (!intervalNumbers.contains(intervalNumber)) {
//                intervalNumbers.add(intervalNumber);
//            }
//
//            // Move to the next interval
//            currentDate = baseYearStart.plusDays(intervalNumber * intervalType);

            if (intervalNumber > getMaxNumberOfBlocks(365, intervalType)) {
                intervalNumber -= 1;
            }
            if (!intervalNumbers.contains(intervalNumber)) {
                intervalNumbers.add(intervalNumber);
            }
          //  intervalNumbers.add(intervalNumber);

            // Move to the next interval based on intervalType days
            currentDate = currentDate.plusDays(intervalType);
        }
        Collections.sort(intervalNumbers);
        return intervalNumbers;
    }

    // Calculate the interval number for a specific date, using a fixed non-leap year (2023)
    public int calculateIntervalNumberForDate(LocalDate startDate) {


        // Use the fixed base year 2023 to calculate the interval number
        LocalDate baseYearStart = LocalDate.of(2023, 1, 1);
        int intervalNumber = (int) ChronoUnit.DAYS.between(
                baseYearStart, startDate.withYear(2023)
        ) / intervalType + 1;

        if (intervalNumber > getMaxNumberOfBlocks(365, intervalType)) {
            return intervalNumber - 1;
        }
        return intervalNumber;
    }

    // find max duration
    public Integer findMaxDuration(List<Bed> bedList) {
        double maxDuration = 0;

        for (Bed bed : bedList) {
            for (CropBedSection cropBedSection : bed.getCropBed()) {
                if (cropBedSection.getCrop() != null) {
                    System.out.println("crop name :" + cropBedSection.getCrop().getName());
                    double duration = cropBedSection.getCrop().getDuration(); // duration in days
                    double stretch = cropBedSection.getStretch(); // stretch in percentage
                    double effectiveDuration = duration * (1 + (stretch / 100));

                    if (effectiveDuration > maxDuration) {
                        maxDuration = effectiveDuration;
                    }
                }
            }
        }
        return (int) Math.round(maxDuration);
    }


    public List<Months> generateMonths() {
        int daysInYear = 364;

        List<Months> months = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = startDate.plusDays(daysInYear); //31 dec 2023
        Map<String, List<String>> monthsMap = new LinkedHashMap<>();
        monthsMap.put("January", new ArrayList<>());
        monthsMap.put("February", new ArrayList<>());
        monthsMap.put("March", new ArrayList<>());
        monthsMap.put("April", new ArrayList<>());
        monthsMap.put("May", new ArrayList<>());
        monthsMap.put("June", new ArrayList<>());
        monthsMap.put("July", new ArrayList<>());
        monthsMap.put("August", new ArrayList<>());
        monthsMap.put("September", new ArrayList<>());
        monthsMap.put("October", new ArrayList<>());
        monthsMap.put("November", new ArrayList<>());
        monthsMap.put("December", new ArrayList<>());

        while (!startDate.isAfter(endDate)) {
            int intervalNumber = calculateIntervalNumberForDate(startDate);

            String currentMonth = startDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            // Initialize list if month not already in the map
            //  monthsMap.putIfAbsent(currentMonth, new ArrayList<>());

            // Add the interval number (e.g., "W1", "W2") if not already added for this month
            String weekLabel = "W" + intervalNumber;
            if (!monthsMap.get(currentMonth).contains(weekLabel)) {
                monthsMap.get(currentMonth).add(weekLabel);
            }

            // Move to the next interval start date
            startDate = startDate.plusDays(intervalType);
        }
        // Convert the map to a list of Months objects
        months = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : monthsMap.entrySet()) {
            months.add(new Months(entry.getKey(), entry.getValue()));
        }

        return months;
    }

    public int getMaxNumberOfBlocks(Integer simulationDurationInDays, Integer simulationRunPolicy) {
        return simulationDurationInDays / simulationRunPolicy;
    }

    // convert weeks integer list into string list
    public List<String> convertWeeksIntoStringForm(List<Integer> listOfWeeksInInteger) {
        List<String> weeks = listOfWeeksInInteger.stream()
                .map(week -> "W" + week)
                .collect(Collectors.toList());

        return weeks;
    }



}



