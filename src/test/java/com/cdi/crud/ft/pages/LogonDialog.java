package com.cdi.crud.ft.pages;

import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;

import static org.jboss.arquillian.graphene.Graphene.guardAjax;

public class LogonDialog {

    @Root
    private GrapheneElement dialog;

    @FindByJQuery("input[id$=user]")
    private GrapheneElement user;

    @FindByJQuery("button[id$=btLogin]")
    private GrapheneElement btLogin;



    public void doLogon(String user){
        this.user.clear();
        this.user.sendKeys(user);
        guardAjax(btLogin).click();
    }

}
