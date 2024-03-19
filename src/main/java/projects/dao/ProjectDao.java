
package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

   
    	
    	  private static final String CATEGORY_TABLE = "category";
    	  private static final String MATERIAL_TABLE = "material";
    	  private static final String PROJECT_TABLE = "project";
    	  private static final String PROJECT_CATEGORY_TABLE = "project_category";
    	  private static final String STEP_TABLE = "step";
    	  
    	 
   
    	  public Project insertProject(Project project) {
    		    /*
    		     * Note that the primary key (recipe_id) is not included in the list of
    		     * fields in the insert statement. MySQL will set the correct primary key
    		     * value when the row is inserted.
    		     */
    		    // @formatter:off
    		    String sql = ""
    		        + "INSERT INTO " + PROJECT_TABLE + " "
    		        + "(project_name, notes, estimated_hours, actual_hours, difficulty) "
    		        + "VALUES "
    		        + "(?, ?, ?, ?, ?)";
    		    // @formatter:on

    		    try (Connection conn = DbConnection.getConnection()) {
    		      startTransaction(conn);

    		      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		        setParameter(stmt, 1, project.getProjectName(), String.class);
    		        setParameter(stmt, 2, project.getNotes(), String.class);
    		        setParameter(stmt, 3, project.getEstimatedHours(), BigDecimal.class);
    		        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
    		        setParameter(stmt, 5, project.getActualHours(), BigDecimal.class);
    		        
    		        
    		        
    		        stmt.executeUpdate();
    		    
    		        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);

    		        commitTransaction(conn);

    		        project.setProjectId(projectId);
    		        return project;
    		        
    		        
    		      } catch (Exception e) {
    		        rollbackTransaction(conn);
    		        throw new DbException(e);
    		      }
    		    } catch (SQLException e) {
    		      throw new DbException(e);
    		    }
    		  }


    	  public List<Project> fetchAllProjects() {
    		    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

    		    try (Connection conn = DbConnection.getConnection()) {
    		        startTransaction(conn);
    		        
    		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		           
    		        		try (ResultSet rs = stmt.executeQuery()) {

    		            List<Project> projects = new LinkedList<Project>();
    		            
    		            while (rs.next()) {
    		                projects.add(extract(rs, Project.class));
    		            }
    		            
    		            return projects;
    		        		}
    		        } catch (SQLException e) {
    		            rollbackTransaction(conn);
    		            throw new DbException("SQL error while executing fetchAllProjects()", e);
    		        }

    		    } catch (Exception e) {
    		        throw new DbException("Error in fetchAllProjects()", e);
    		    }
    		}
    	  public Optional<Project> fetchProjectById(Integer projectId) {
    	        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
    	        
    	        try (Connection conn = DbConnection.getConnection()) {
    	            startTransaction(conn);
    	            
    	            try {
    	            Project project = null;
    	            
    	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    	            	setParameter(stmt, 1, projectId, Integer.class);
    	                
    	                try (ResultSet rs = stmt.executeQuery()) {
    	                    if (rs.next()) {
    	                        project = extract(rs, Project.class);
    	                    }
    	                }
    	            }
							if (Objects.nonNull(project)) {
								project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
								project.getSteps().addAll(fetchStepsForProject(conn, projectId));
								project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
							}
						   commitTransaction(conn); 
					            return Optional.ofNullable(project);
					            
    	            } catch (Exception e) {
    	            	rollbackTransaction(conn);
    	         
    	                throw new DbException(e.toString());
    	            }
    	        }  catch (SQLException e) {
    	                throw new DbException(e.toString());
    	            }
			} 

			 private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) {
			        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
			        
			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            setParameter(stmt, 1, projectId, Integer.class);
			            
			            try (ResultSet rs = stmt.executeQuery()) {
			                List<Material> materials = new LinkedList<Material>();
			                
			                while (rs.next()) {
			                    materials.add(extract(rs, Material.class));
			                }
			                
			                return materials;
			            }
			        } catch (SQLException e) {
			            throw new DbException("Error fetching materials for project", e);
			        }
			    }

			 private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
				    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
				    
				    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				    	setParameter(stmt, 1, projectId, Integer.class);
				        
				        try (ResultSet rs = stmt.executeQuery()) {
				            List<Step> steps = new LinkedList<Step>();
				            
				            while (rs.next()) {
				                steps.add(extract(rs, Step.class));
				            }
				            
				            return steps;
				        }
				    }
				}
			
			 private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
				    String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c " +
				                 "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) " +
				                 "WHERE project_id = ?";
				    
				    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				    	setParameter(stmt, 1, projectId, Integer.class);
				        
				        try (ResultSet rs = stmt.executeQuery()) {
				            List<Category> categories = new LinkedList<Category>();
				            
				            while (rs.next()) {
				                categories.add(extract(rs, Category.class));
				            }
				            
				            return categories;
				        }
				    }
				}

}
    		      
    
    
