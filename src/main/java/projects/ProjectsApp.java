package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
    private Scanner scanner = new Scanner(System.in);
    private ProjectService projectService = new ProjectService();
    private Project curProject;
    private List<String> operations = List.of(
            "1) Add a Project",
    	    "2) List All Project",
    	    "3) Select a Project"
    		
    );

    
    void processUserSelections() {
        boolean done = false;

        while (!done) {
            try {
                int selection = getUserSelection();
                switch (selection) {
                    case -1:
                        done = exitMenu();
                        break;
                    case 1:
                        addProject();
                        break;
                    case 2:
                        listProjects();
                        break;
                    case 3:
                        selectProject();
                        break;
                    
                    default:
                        System.out.println("\n" + selection + " is not valid. Try again.");
                        break;
                }
            } catch (DbException e) {
                System.out.println("\nError: " + e.getMessage() + " Try Again.");
            }
        }
    }
    
    private void listProjects() {

  		// TODO Auto-generated method stub

      			List<Project> projects = projectService.fetchAllProjects();

      			System.out.println("\nProjects:");

      			projects.forEach(project -> System.out.println("  "  + project.getProjectId() + ": " + project.getProjectName()));   

      	

  	}

  	private void selectProject() {

  		// TODO Auto-generated method stub

  		listProjects(); 
  		Integer projectId = getIntInput("Enter the project you would like to pick!");
  		curProject = null;
  		curProject = projectService.fetchProjectById(projectId);

  	}

    private void addProject() {
        String projectName = getStringInput("Enter the Project name");
        Integer difficulty = getIntInput("Enter the difficulty 1-5");
        String notes = getStringInput("Enter the project notes");
        BigDecimal estimatedHours = getDecimalInput("Enter estimated time worked");
        BigDecimal actualHours = getDecimalInput("Enter actual time worked");

        Project project = new Project();
        project.setProjectName(projectName);
        project.setDifficulty(difficulty);
        project.setNotes(notes);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);

        Project dbProject = projectService.addProject(project);
        System.out.println("You've Made your Project !! " + dbProject);
        
        System.out.print("You've made a connection" + dbProject);
    }

    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }

        try {
            return new BigDecimal(input).setScale(2);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. ");
        }
    }

    private boolean exitMenu() {
        System.out.println("\nExiting the menu.");
        return true;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String line = scanner.nextLine();
        return line.isBlank() ? null : line.trim();
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu Selection");
        return Objects.isNull(input) ? -1 : input;
    }

    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            throw new DbException(input + " is not a valid number. ");
        }
    }

    private void printOperations() {
        System.out.println("\nThese are the selections. Press a key to pick one!");
        operations.forEach(line -> System.out.println(" " + line));
        
        if (Objects.isNull(curProject)) {
            System.out.println("\nPick another project");
        } else { 
            System.out.println("\nThe project you are working with is: " + curProject);
        }
    }
}

 
 
    
