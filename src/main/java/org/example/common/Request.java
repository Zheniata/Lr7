package org.example.common;

import org.example.common.models.Organization;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Object argument;
    private Organization organization;

    public Request(String name, Object argument, Organization organization){
        this.name = name;
        this.argument = argument;
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public Object getArgument() {
        return argument;
    }

    public Organization getOrganization() {
        return organization;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                '}';
    }
}
