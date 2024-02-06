module minesweeperrest {
    // Jackson 
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires transitive minesweepercore;

    // SpringBoot
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.beans;
    requires spring.core;
    requires spring.context;

    // SpringBoot needs access to the springBoot package, so called 'deep reflection'.
    opens springboot;
}
