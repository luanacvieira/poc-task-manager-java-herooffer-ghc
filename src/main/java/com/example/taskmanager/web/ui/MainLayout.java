package com.example.taskmanager.web.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    public MainLayout() {
        H1 title = new H1("Task Manager Monolith");
        title.getStyle().set("font-size","1.2rem");
        HorizontalLayout header = new HorizontalLayout(title, new RouterLink("Tarefas", TaskView.class));
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.getStyle().set("padding","0 1rem");
        addToNavbar(header);
    }
}
