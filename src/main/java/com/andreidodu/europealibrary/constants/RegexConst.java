package com.andreidodu.europealibrary.constants;

import java.util.regex.Pattern;

public interface RegexConst {
    String USERNAME = "^[A-Za-z][A-Za-z0-9_]{4,36}$";
    String PASSWORD = "^(?=.*[0-9])(?=.*[!@#$%^&*_:;,.])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9!@#$%^&*_:;,.]{5,50}$";
    String YEAR = "^([0-9]{4})(?=[-\\.]?)";
}
