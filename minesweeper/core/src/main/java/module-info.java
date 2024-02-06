module minesweepercore {
    opens core;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports core;
    exports core.settings;
    exports core.savehandler;
}
