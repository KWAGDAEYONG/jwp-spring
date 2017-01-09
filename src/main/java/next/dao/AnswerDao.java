package next.dao;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import next.model.Answer;

@Repository
public class AnswerDao {
	
	
	@Autowired
	private static AnswerDao answerDao;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private AnswerDao() {}
	
	public static AnswerDao getInstance() {
		if (answerDao == null) {
			answerDao = new AnswerDao();
		}
		return answerDao;
	}
	
    public Answer insert(Answer answer) {
        String sql = "INSERT INTO ANSWERS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";
        PreparedStatementCreator psc = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, answer.getWriter());
				pstmt.setString(2, answer.getContents());
				pstmt.setTimestamp(3, new Timestamp(answer.getTimeFromCreateDate()));
				pstmt.setLong(4, answer.getQuestionId());
				return pstmt;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);
        return findById((long)keyHolder.getKey());
    }

    public Answer findById(long answerId) {
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE answerId = ?";

        RowMapper<Answer> rm = new RowMapper<Answer>() {
            

			@Override
			public Answer mapRow(ResultSet rs, int arg1) throws SQLException {
				return new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"),
                        rs.getTimestamp("createdDate"), rs.getLong("questionId"));
			}
        };

        return jdbcTemplate.queryForObject(sql, rm, answerId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        String sql = "SELECT answerId, writer, contents, createdDate FROM ANSWERS WHERE questionId = ? "
                + "order by answerId desc";

        RowMapper<Answer> rm = new RowMapper<Answer>() {

			@Override
			public Answer mapRow(ResultSet rs, int arg1) throws SQLException {
				return new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"),
                        rs.getTimestamp("createdDate"), questionId);
			}
        };

        return jdbcTemplate.query(sql, rm, questionId);
    }

	public void delete(Long answerId) {
        String sql = "DELETE FROM ANSWERS WHERE answerId = ?";
        jdbcTemplate.update(sql, answerId);
	}
}
