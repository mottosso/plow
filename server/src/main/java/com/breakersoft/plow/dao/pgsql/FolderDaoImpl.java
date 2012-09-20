package com.breakersoft.plow.dao.pgsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.breakersoft.plow.Folder;
import com.breakersoft.plow.FolderE;
import com.breakersoft.plow.Project;
import com.breakersoft.plow.dao.AbstractDao;
import com.breakersoft.plow.dao.FolderDao;
import com.breakersoft.plow.util.JdbcUtils;

@Repository
public class FolderDaoImpl extends AbstractDao implements FolderDao {

    public static final RowMapper<Folder> MAPPER = new RowMapper<Folder>() {
        @Override
        public Folder mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            FolderE folder = new FolderE();
            folder.setFolderId((UUID)rs.getObject(1));
            folder.setProjectId((UUID)rs.getObject(2));
            return folder;
        }
    };

    private static final String GET =
            "SELECT " +
                "pk_folder,"+
                "pk_project " +
            "FROM " +
                "plow.folder " +
            "WHERE " +
                "pk_folder = ?";

    @Override
    public Folder get(UUID id) {
        return jdbc.queryForObject(GET, MAPPER, id);
    }

    private static final String INSERT =
            JdbcUtils.Insert("plow.folder",
                "pk_folder",
                "pk_parent",
                "pk_project",
                "str_name");

    @Override
    public Folder createFolder(Project project, String name) {
        UUID id = UUID.randomUUID();
        jdbc.update(INSERT, id, null, project.getProjectId(), name);

        FolderE folder = new FolderE();
        folder.setFolderId(id);
        folder.setProjectId(project.getProjectId());
        return folder;
    }

}
