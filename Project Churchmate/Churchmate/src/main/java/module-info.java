module Churchmate {
    requires java.sql;
    requires javafx.controls;
    requires javafx.graphics;

    opens com.churchmate;
    opens com.churchmate.ui;
    opens com.churchmate.dao;
    opens com.churchmate.model;
    opens com.churchmate.service;
    opens com.churchmate.controller;
}