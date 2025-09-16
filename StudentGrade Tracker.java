import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class StudentGradeTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> grades = new ArrayList<>();

        System.out.print("Enter the number of students: ");
        int numStudents = validateInput(scanner, 1, Integer.MAX_VALUE);

        for (int i = 0; i < numStudents; i++) {
            int grade = getValidGrade(scanner, i + 1);
            grades.add(grade);
        }

        double average = calculateAverage(grades);
        int highest = findHighest(grades);
        int lowest = findLowest(grades);

        // Final Result Display
        System.out.println("\n===== Student Grades Report =====");
        System.out.printf("Average grade: %.2f%n", average);
        System.out.println("Highest grade: " + highest);
        System.out.println("Lowest grade: " + lowest);

        // Save grades to file
        saveGradesToFile(grades);

        scanner.close();
    }

    public static int validateInput(Scanner scanner, int min, int max) {
        int input;
        while (true) {
            try {
                input = scanner.nextInt();
                if (input < min || input > max) {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max);
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // clear invalid input
            }
        }
        return input;
    }

    public static int getValidGrade(Scanner scanner, int studentNumber) {
        int grade;
        while (true) {
            System.out.print("Enter grade for student " + studentNumber + ": ");
            grade = validateInput(scanner, 0, 100);
            if (grade >= 0 && grade <= 100) {
                break;
            } else {
                System.out.println("Invalid grade. Please enter a grade between 0 and 100.");
            }
        }
        return grade;
    }

    public static double calculateAverage(ArrayList<Integer> grades) {
        int sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        return (double) sum / grades.size();
    }

    public static int findHighest(ArrayList<Integer> grades) {
        int highest = grades.get(0);
        for (int grade : grades) {
            if (grade > highest) {
                highest = grade;
            }
        }
        return highest;
    }

    public static int findLowest(ArrayList<Integer> grades) {
        int lowest = grades.get(0);
        for (int grade : grades) {
            if (grade < lowest) {
                lowest = grade;
            }
        }
        return lowest;
    }

    public static void saveGradesToFile(ArrayList<Integer> grades) {
        try (FileWriter writer = new FileWriter("grades.txt")) {
            writer.write("===== Grades Report =====\n");
            for (int i = 0; i < grades.size(); i++) {
                writer.write("Student " + (i + 1) + ": " + grades.get(i) + "\n");
            }
            System.out.println("Grades saved to grades.txt");
        } catch (IOException e) {
            System.out.println("Error saving grades to file: " + e.getMessage());
        }
    }
}
