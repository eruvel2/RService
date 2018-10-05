package com.springapp.mvc;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/11/14
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecipeService {

    private Logger log = Logger.getLogger("RecipeService");
    private ApplicationContext context;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    public RecipeService(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<String> getAllRecipes(String category, Boolean isAdmin) {
        log.info("Starting getAllRecipes");
        StringBuilder sql = new StringBuilder("select distinct name"  +
                " from recipe.recipe where 1=1 ");
        if (StringUtils.isNotBlank(category) && !category.equals("All")) {
                sql.append(" and category = '").append(
                          category).append("'");
        }
        if (!isAdmin) {
                sql.append(" and hidden = 'N").append("'");
        }
        sql.append(" order by name");

        List<String> names = this.jdbcTemplate.query(sql.toString(), new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("name");
            }
        });
        log.info("Returning " + names.size() + " rows");
        return names;
    }

    public List<String> getCategories() {
           log.info("Starting getCategories");
           String sql = "select distinct category"  +
                   " from recipe.recipe " +
                   "where category is not null " +
                   "order by category";

           List<String> names = this.jdbcTemplate.query(sql, new RowMapper() {

               public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                   return rs.getString("category");
               }
           });
           log.info("Returning " + names.size() + " rows");
           return names;
    }

    @SuppressWarnings("unchecked")
    public List<RecipeDetails> findRecipe(Boolean admin, Map<String, String> config) {
        log.info("Starting findRecipe");
        String sql = "select ID, name, temperature, cooktime, ingredient1, " +
                " ingredient2, ingredient3, ingredient4, ingredient5, " +
                " ingredient6, ingredient7, ingredient8, ingredient9, ingredient10, " +
                " ingredient11, ingredient12, ingredient13, ingredient14, ingredient15, " +
                " ingredient16, ingredient17, ingredient18, ingredient19, " +
                " ingredient20, ingredient21, ingredient22, ingredient23, " +
                " ingredient24, ingredient25, user, category, hidden "  +
                " from recipe.recipe ";

        StringBuilder whereClause = new StringBuilder("where 1 = 1 ");
        String recipeName = config.get("name");
        if (StringUtils.isNotBlank(recipeName)) {
            whereClause.append(" and name like '%").append(
                    recipeName).append("%'");
        }
        String category = config.get("category");
        if (StringUtils.isNotBlank(category) && !category.equals("All")) {
            whereClause.append(" and category = '").append(
                    category).append("'");
        }
        if (!admin) {
            whereClause.append(" and hidden = 'N").append("'");
        }
        String orderBy = " order by name";

        List<RecipeDetails> recipes = this.jdbcTemplate.query(sql
                + whereClause.toString() + orderBy, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RecipeDetails recipeDetails = new RecipeDetails();
                recipeDetails.setId(rs.getInt("ID"));
                recipeDetails.setName(rs.getString("name"));
                recipeDetails.setCooktime(rs.getString("cooktime"));
                recipeDetails.setTemperature(rs.getInt("temperature"));
                recipeDetails.setIngredient1(rs.getString("ingredient1"));
                recipeDetails.setIngredient2(rs.getString("ingredient2"));
                recipeDetails.setIngredient3(rs.getString("ingredient3"));
                recipeDetails.setIngredient4(rs.getString("ingredient4"));
                recipeDetails.setIngredient5(rs.getString("ingredient5"));
                recipeDetails.setIngredient6(rs.getString("ingredient6"));
                recipeDetails.setIngredient7(rs.getString("ingredient7"));
                recipeDetails.setIngredient8(rs.getString("ingredient8"));
                recipeDetails.setIngredient9(rs.getString("ingredient9"));
                recipeDetails.setIngredient10(rs.getString("ingredient10"));
                recipeDetails.setIngredient11(rs.getString("ingredient11"));
                recipeDetails.setIngredient12(rs.getString("ingredient12"));
                recipeDetails.setIngredient13(rs.getString("ingredient13"));
                recipeDetails.setIngredient14(rs.getString("ingredient14"));
                recipeDetails.setIngredient15(rs.getString("ingredient15"));
                recipeDetails.setIngredient16(rs.getString("ingredient16"));
                recipeDetails.setIngredient17(rs.getString("ingredient17"));
                recipeDetails.setIngredient18(rs.getString("ingredient18"));
                recipeDetails.setIngredient19(rs.getString("ingredient19"));
                recipeDetails.setIngredient20(rs.getString("ingredient20"));
                recipeDetails.setIngredient21(rs.getString("ingredient21"));
                recipeDetails.setIngredient22(rs.getString("ingredient22"));
                recipeDetails.setIngredient23(rs.getString("ingredient23"));
                recipeDetails.setIngredient24(rs.getString("ingredient24"));
                recipeDetails.setIngredient25(rs.getString("ingredient25"));
                recipeDetails.setHidden((rs.getString("hidden")));
                recipeDetails.setUser(rs.getString("user"));
                recipeDetails.setCategory(rs.getString("category"));

                return recipeDetails;
            }
        });
        log.info("Returning " + recipes.size() + " rows");

        int start = new Integer(config.get("offset"));
        int limit = recipes.size();

        ArrayList<RecipeDetails> subItemVOList = new ArrayList<RecipeDetails>();

        int configOffset = new Integer(config.get("offset"));
        int configLimit = new Integer(config.get("limiy"));

        if (configLimit > 0) {
            limit = Math.min(start + configLimit, limit);
        }
        int offset = configOffset < 0 ? 0 : configOffset;
        for (int i = offset; i < limit; i++) {
            subItemVOList.add(recipes.get(i));
        }

        log.info("Finished findRecipe");
        return subItemVOList;

    }

/*

 @Override
    public String createPassword(String user, String password) {

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        this.jdbcTemplate.update(
                "insert recipe.users (user, password) "
                + "values (?, ?) ", new Object[] { user, hash });
        return "Insert successful";

    }

*/

    public String checkAdmin(String user) {
        log.info("Checking admin for user " + user);
        String message = "";
           boolean valid = false;
           try {
               String admin = (String) this.jdbcTemplate.queryForObject(
                       "select admin from recipe.users "
                               + "where user = ? ", new Object[] { user },
                       new RowMapper() {

                           public Object mapRow(ResultSet rs, int rowNum)
                                   throws SQLException {
                               String admin = rs.getString("admin");
                               return admin;
                           }
                       });
               message = admin;

           } catch (final EmptyResultDataAccessException e) {
               message = "Error Checking for Admin";
               log.info(user + "Error Checking for Admin" + e);
        }
        return message;

    }

    public String addRecipe(RecipeDetails recipe) {
        log.info("Starting addRecipe for recipe " + recipe.getName());
        this.jdbcTemplate.update(
                "insert into recipe.recipe (name, temperature, cooktime, ingredient1, " +
                " ingredient2, ingredient3, ingredient4, ingredient5, " +
                " ingredient6, ingredient7, ingredient8, ingredient9, ingredient10, " +
                " ingredient11, ingredient12, ingredient13, ingredient14, ingredient15, " +
                " ingredient16, ingredient17, ingredient18, ingredient19, " +
                " ingredient20, ingredient21, ingredient22, ingredient23, " +
                " ingredient24, ingredient25, hidden, User, category) values (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, " +
                        "?,?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)",
                new Object[] {recipe.getName(), recipe.getTemperature(), recipe.getCooktime(), recipe.getIngredient1(),
                        recipe.getIngredient2(),
                        recipe.getIngredient3(),
                        recipe.getIngredient4(),
                        recipe.getIngredient5(),
                        recipe.getIngredient6(),
                        recipe.getIngredient7(),
                        recipe.getIngredient8(),
                        recipe.getIngredient9(),
                        recipe.getIngredient10(),
                        recipe.getIngredient11(),
                        recipe.getIngredient12(),
                        recipe.getIngredient13(),
                        recipe.getIngredient14(),
                        recipe.getIngredient15(),
                        recipe.getIngredient16(),
                        recipe.getIngredient17(),
                        recipe.getIngredient18(),
                        recipe.getIngredient19(),
                        recipe.getIngredient20(),
                        recipe.getIngredient21(),
                        recipe.getIngredient22(),
                        recipe.getIngredient23(),
                        recipe.getIngredient24(),
                        recipe.getIngredient25(),
                        recipe.getHidden(),
                        recipe.getUser(),
                        recipe.getCategory()});

        log.info("Recipe insert Successful for recipe " + recipe.getName());
        return "Insert successful";

    }

    public String updateRecipe(RecipeDetails recipe) {
        log.info("Starting updateRecipe for recipe " + recipe.getName());
        this.jdbcTemplate.update(
                "update recipe.recipe set name=?, temperature=?, cooktime=?, ingredient1=?, " +
                " ingredient2=?, ingredient3=?, ingredient4=?, ingredient5=?, " +
                " ingredient6=?, ingredient7=?, ingredient8=?, ingredient9=?, ingredient10=?, " +
                " ingredient11=?, ingredient12=?, ingredient13=?, ingredient14=?, ingredient15=?, " +
                " ingredient16=?, ingredient17=?, ingredient18=?, ingredient19=?, " +
                " ingredient20=?, ingredient21=?, ingredient22=?, ingredient23=?, " +
                " ingredient24=?, ingredient25=?, User=?, category=?, hidden=? " +
                " where ID=?",
                new Object[] {recipe.getName(), recipe.getTemperature(), recipe.getCooktime(), recipe.getIngredient1(),
                        recipe.getIngredient2(),
                        recipe.getIngredient3(),
                        recipe.getIngredient4(),
                        recipe.getIngredient5(),
                        recipe.getIngredient6(),
                        recipe.getIngredient7(),
                        recipe.getIngredient8(),
                        recipe.getIngredient9(),
                        recipe.getIngredient10(),
                        recipe.getIngredient11(),
                        recipe.getIngredient12(),
                        recipe.getIngredient13(),
                        recipe.getIngredient14(),
                        recipe.getIngredient15(),
                        recipe.getIngredient16(),
                        recipe.getIngredient17(),
                        recipe.getIngredient18(),
                        recipe.getIngredient19(),
                        recipe.getIngredient20(),
                        recipe.getIngredient21(),
                        recipe.getIngredient22(),
                        recipe.getIngredient23(),
                        recipe.getIngredient24(),
                        recipe.getIngredient25(),
                        recipe.getUser(),
                        recipe.getCategory(),
                        recipe.getHidden(),
                        recipe.getId()});

        log.info("Recipe update Successful for recipe " + recipe.getName());
        return "Update successful";

    }


    public Integer getRecipe(String recipeName) {
        log.info("Starting getRecipe");
        String sql = "select name"  +
                " from recipe.recipe " +
                " order by name";

        List<String> names = this.jdbcTemplate.query(sql, new RowMapper() {

            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("name");
            }
        });
        int recordNumber = names.indexOf(recipeName);
        log.info("Record number of " + recipeName + " = " + recordNumber);
        return recordNumber;
    }


    @SuppressWarnings("unchecked")
    public List<String> getRecipeNamesForCategory(String category) {
        log.info("Starting getNames");
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct name from recipe.recipe");
        if (category != null && !category.equals("All")) {
            sql.append(" where category = '" + category + "'");
        }
        List<String> names = this.jdbcTemplate.query(sql
                .toString(), new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("name");
            }
        });

        log.info("Returning " + names.size() + " names");
       return names;
    }
    @SuppressWarnings("unchecked")
    public List<String> getRecipeNamesStartingWith(String recipe) {
        log.info("Starting getRecipeNamesStartingWith");
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct name from recipe.recipe");
        if (recipe != null ) {
            sql.append(" where upper(name) like '" + recipe.toUpperCase() + "%'");
        }
        List<String> names = this.jdbcTemplate.query(sql
                .toString(), new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("name");
            }
        });

        log.info("Returning " + names.size() + " names");
       return names;
    }
    public boolean login (String user, String password) {
        log.info("Starting login for user " + user);
        String message = "";
        boolean valid = false;
        try {
            String dbPassword = (String) this.jdbcTemplate.queryForObject(
                    "select password from recipe.users "
                            + "where user = ? ", new Object[] { user },
                    new RowMapper() {

                        public Object mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            String pass = rs.getString("password");
                            return pass;
                        }
                    });
            valid = BCrypt.checkpw(password, dbPassword);

        } catch (final EmptyResultDataAccessException e) {
            message = "Wrong Username or Password.";
            log.info(user + "Bad username/password" + e);
        }
        return valid;
    }

}

