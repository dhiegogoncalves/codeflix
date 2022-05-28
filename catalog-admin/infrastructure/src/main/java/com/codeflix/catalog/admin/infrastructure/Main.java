package com.codeflix.catalog.admin.infrastructure;

import com.codeflix.catalog.admin.application.UserCase;

public class Main {
    public static void main(String[] args) {
        System.out.println(new UserCase().execute());
    }
}