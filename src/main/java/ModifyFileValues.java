import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ModifyFileValues {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/2023_order_details.txt");

        List<String> modifiedLines = new ArrayList<>();

        for (String line : Files.readAllLines(path)) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                try {
                    int first = Integer.parseInt(parts[0].trim());
                    int second = Integer.parseInt(parts[1].trim());
                    second += 50;
                    int third = Integer.parseInt(parts[2].trim());
                    if (third > 20) {third -= 20;} else {third = 10;}

                    modifiedLines.add(first + "," + second + "," + third);
                } catch (NumberFormatException e) {
                    // skip malformed lines
                    System.err.println("Skipping invalid line: " + line);
                }
            }
        }

        Files.write(path, modifiedLines);
    }
}
