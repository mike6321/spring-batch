CREATE TABLE PERSON (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(255),
    AGE VARCHAR(255),
    ADDRESS VARCHAR(255)
);

INSERT INTO PERSON(NAME, AGE, ADDRESS)
VALUES ('최준우',32,'서울');
INSERT INTO PERSON(NAME, AGE, ADDRESS)
VALUES ('홍길동',30,'인천');
INSERT INTO PERSON(NAME, AGE, ADDRESS)
VALUES ('아무개',25,'강원');