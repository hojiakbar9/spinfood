package io;

import com.opencsv.CSVWriter;
import models.data.PairModel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVDataWriter {

    public static void writePairsListToCSV(String filePath, List<PairModel> pairs) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath), ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            for (PairModel pair : pairs) {
                String[] data = {
                        pair.getParticipant1().getName(),
                        pair.getParticipant2() != null ? pair.getParticipant2().getName() : "",
                        String.valueOf(pair.isJointRegistration() ? 2 : 1),
                        String.valueOf(pair.getKitchenLongitude()),
                        String.valueOf(pair.getKitchenLatitude()),
                        pair.getMainFoodPreference().toString(),
                        String.valueOf(pair.getPairNumber()),
                        String.valueOf(pair.getAppetizerGroupNumber()),
                        String.valueOf(pair.getMainCourseGroupNumber()),
                        String.valueOf(pair.getDessertGroupNumber()),
                        String.valueOf(pair.isKitchenSupplier()),
                        String.valueOf(pair.getCookingCourseNumber() != null ? pair.getCookingCourseNumber() : "null"),
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}