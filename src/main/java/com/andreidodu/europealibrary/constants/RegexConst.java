package com.andreidodu.europealibrary.constants;

public interface RegexConst {
    String USERNAME = "^[A-Za-z][A-Za-z0-9_]{4,36}$";
    String PASSWORD = "^(?=.*[0-9])(?=.*[!@#$%^&*_:;,.])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9!@#$%^&*_:;,.]{5,50}$";
}
