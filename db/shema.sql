CREATE TABLE users (
user_id integer auto_increment PRIMARY KEY,
login varchar(255) NOT NULL,
role_id integer NOT NULL,
CONSTRAINT fk_users_role foreign key (role_id) references roles (role_id)
);

CREATE TABLE roles (
role_id integer auto_increment PRIMARY KEY,
role_name varchar(5) NOT NULL
);

CREATE TABLE tests (
test_id integer auto_increment PRIMARY KEY,
topic varchar(255) NOT NULL UNIQUE
);

CREATE TABLE questions (
question_id integer auto_increment PRIMARY KEY,
question_text varchar(1000) NOT NULL,
test_id integer NOT NULL,
CONSTRAINT fk_questions_test foreign key (test_id) references tests (test_id)
);

CREATE TABLE answers (
answer_id integer auto_increment PRIMARY KEY,
answer_text varchar(1000) NOT NULL,
is_answer_correct BOOLEAN NOT NULL,
question_id integer NOT NULL,
CONSTRAINT fk_answers_question foreign key (question_id) references questions (question_id)
);

CREATE TABLE results (
result_id integer auto_increment PRIMARY KEY,
correct_answers_amount integer NOT NULL,
test_data timestamp NOT NULL,
test_id integer NOT NULL,
user_id integer NOT NULL,
CONSTRAINT fk_results_user foreign key (user_id) references users (user_id),
CONSTRAINT fk_results_test foreign key (test_id) references tests (test_id)
);

INSERT INTO roles (role_name)
VALUES ('admin'), ('user');

INSERT INTO users (login, role_id)
VALUES ('admin', 1), 
	   ('dasha', 2);
	   
INSERT INTO tests (topic)
VALUES ('Личные местоимения в английском языке'),
	   ('Употребление слов little и few'),
	   ('Употребление слов much и many');
	   
INSERT INTO questions (question_text, test_id)
VALUES ('Mary likes animals. ___ bought a cat yesterday.', 1),
	   ('John gave ___ a newspaper. I read much interesting in it.', 1),
	   ('Sara asked me a book. ___ gave it to ___ .', 1),
	   ('A jeweller add too ___ gold to the alloy.', 2),
	   ('There are very ___ computers in our computer sciences room in the school.', 2),
	   ('We have sold so ___ shirts today.', 2),
	   ('He didn’t have ___ broken glass on his body after crash.', 3),
	   ('Did you find ___ information about animals of the Atlantic Ocean?', 3),
	   ('Did you get ___ invitations yesterday?', 3);
	   
INSERT INTO answers (answer_text, is_answer_correct, question_id)
VALUES ('she', TRUE, 1),
	   ('he', FALSE, 1),
	   ('I', FALSE, 1),
	   ('me', TRUE, 2),
	   ('him', FALSE, 2),
	   ('her', FALSE, 2),
	   ('I, her', TRUE, 3),
	   ('me, she', FALSE, 3),
	   ('she, I', FALSE, 3),
	   ('little', TRUE, 4),
	   ('few', FALSE, 4),
	   ('a little', FALSE, 4),
	   ('few', TRUE, 5),
	   ('a few', FALSE, 5),
	   ('little', FALSE, 5),
	   ('few', TRUE, 6),
	   ('little', FALSE, 6),
	   ('a few', FALSE, 6),
	   ('much', TRUE, 7),
	   ('many', FALSE, 7),
	   ('much of', FALSE, 7),
	   ('much', TRUE, 8),
	   ('much of', FALSE, 8),
	   ('many', FALSE, 8),
	   ('many', TRUE, 9),
	   ('many of', FALSE, 9),
	   ('much', FALSE, 9);
