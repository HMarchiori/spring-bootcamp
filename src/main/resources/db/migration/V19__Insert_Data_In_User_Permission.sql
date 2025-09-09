INSERT INTO user_permission (id_user, id_permission) VALUES
                                                         (1,1),(2,1),(1,2)
ON DUPLICATE KEY UPDATE id_user = VALUES(id_user);