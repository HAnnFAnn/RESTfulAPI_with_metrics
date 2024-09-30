DROP TABLE IF EXISTS task;

CREATE TABLE task (
                      id IDENTITY PRIMARY KEY,
                      header_of_task VARCHAR(50) NOT NULL,
                      description_of_task VARCHAR(100) NOT NULL,
                      date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                      status_of_task SMALLINT NOT NULL DEFAULT 0,
                      date_finish DATE DEFAULT CURRENT_DATE
);

INSERT INTO task (header_of_task, description_of_task, status_of_task) VALUES
                                                                           ('task 1', 'Learn Java programming', 0),
                                                                           ('task 2', 'Create an impressive resume', 0),
                                                                           ('task 3', 'Watch educational videos on YouTube', 0),
                                                                           ('task 4', 'Attend job interviews', 1),
                                                                           ('task 5', 'Enjoy the little things in life', 0);