import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Task5 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagerGUI::new);
    }
}

class StudentManagerGUI extends JFrame {
    private JTextField nameField, rollNumberField, gradeField;
    private JTextArea outputArea;
    private StudentManagementSystem studentManage;

    public StudentManagerGUI() {
        super("Student Manager");
        studentManage = new StudentManagementSystem();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        nameField = new JTextField();
        rollNumberField = new JTextField();
        gradeField = new JTextField();

        inputPanel.add(createLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createLabel("Roll Number:"));
        inputPanel.add(rollNumberField);
        inputPanel.add(createLabel("Grade:"));
        inputPanel.add(gradeField);

        addButton(inputPanel, "Add Student", e -> {
            try {
                studentManage.addStudent(nameField.getText(), Integer.parseInt(rollNumberField.getText()), Integer.parseInt(gradeField.getText()));
                JOptionPane.showMessageDialog(this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } catch (NumberFormatException ex) {
                showError("Invalid input for roll number or grade. Please enter numeric values.");
            }
        });

        addButton(inputPanel, "Display All Students", e -> displayAllStudents());

        addButton(inputPanel, "Remove Student", e -> {
            String nameToRemove = JOptionPane.showInputDialog(this, "Enter the name of the student to remove:");
            if (nameToRemove != null) {
                try {
                    studentManage.removeStudent(nameToRemove);
                    JOptionPane.showMessageDialog(this, "Student removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        addButton(inputPanel, "Edit Student", e -> {
            String nameToEdit = JOptionPane.showInputDialog(this, "Enter the name of the student to edit:");
            if (nameToEdit != null) {
                String[] options = {"Name", "Roll Number", "Grade"};
                int choice = JOptionPane.showOptionDialog(this, "What would you like to change?", "Edit Student",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice != -1) {
                    try {
                        String newValue = JOptionPane.showInputDialog(this, "Enter the new value:");
                        if (newValue != null) {
                            studentManage.editStudent(nameToEdit, choice, newValue);
                            JOptionPane.showMessageDialog(this, "Student edited successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearFields();
                        }
                    } catch (IllegalArgumentException ex) {
                        showError(ex.getMessage());
                    }
                }
            }
        });

        addButton(inputPanel, "Exit", e -> {
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        outputArea = new JTextArea(10, 40);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        studentManage.loadFromFile();
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private JLabel createLabel(String text) {
        return new JLabel(text);
    }

    private void clearFields() {
        nameField.setText("");
        rollNumberField.setText("");
        gradeField.setText("");
    }

    private void displayAllStudents() {
        String studentsInfo = studentManage.getAllStudents();
        if (studentsInfo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students are currently enrolled.", "Students", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, studentsInfo, "Students", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

class Student {
    private String name;
    private int rollNumber;
    private int grade;

    public Student(String name, int rollNumber, int grade) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}

class StudentManagementSystem {
    private ArrayList<Student> students = new ArrayList<>();
    private String filename = "students.txt";

    public void addStudent(String name, int rollNumber, int grade) {
        students.add(new Student(name, rollNumber, grade));
        saveToFile();
    }

    public String getAllStudents() {
        StringBuilder result = new StringBuilder();
        for (Student student : students) {
            result.append("Name: ").append(student.getName()).append(", Roll Number: ").append(student.getRollNumber()).append(", Grade: ").append(student.getGrade()).append("\n");
        }
        return result.toString();
    }

    public void removeStudent(String nameToRemove) {
        boolean found = false;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getName().equalsIgnoreCase(nameToRemove)) {
                students.remove(i);
                found = true;
                saveToFile();
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Student not found.");
        }
    }

    public void editStudent(String nameToEdit, int option, String newValue) {
        boolean found = false;
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(nameToEdit)) {
                switch (option) {
                    case 0:
                        student.setName(newValue);
                        break;
                    case 1:
                        student.setRollNumber(Integer.parseInt(newValue));
                        break;
                    case 2:
                        student.setGrade(Integer.parseInt(newValue));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid option.");
                }
                found = true;
                saveToFile();
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Student not found.");
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Student student : students) {
                writer.println(student.getName() + "," + student.getRollNumber() + "," + student.getGrade());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int rollNumber = Integer.parseInt(parts[1]);
                    int grade = Integer.parseInt(parts[2]);
                    students.add(new Student(name, rollNumber, grade));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
