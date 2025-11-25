package com.example.taskmanager.web.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createDrawer();
    }
    
    private void createHeader() {
        Icon logo = new Icon(VaadinIcon.TASKS);
        logo.getStyle()
            .set("width", "2rem")
            .set("height", "2rem")
            .set("color", "#667eea");
        
        H1 title = new H1("Task Manager");
        title.getStyle()
            .set("font-size", "1.5rem")
            .set("margin", "0")
            .set("color", "#1f2937")
            .set("font-weight", "700");
        
        Span subtitle = new Span("Microsserviços");
        subtitle.getStyle()
            .set("font-size", "0.75rem")
            .set("color", "#6b7280")
            .set("margin-left", "0.5rem");
        
        HorizontalLayout logoLayout = new HorizontalLayout(logo, title, subtitle);
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.setSpacing(true);
        
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logoLayout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setSpacing(true);
        header.getStyle()
            .set("padding", "0.75rem 1.5rem")
            .set("background", "white")
            .set("box-shadow", "0 1px 3px 0 rgba(0, 0, 0, 0.1)");
        
        addToNavbar(header);
    }
    
    private void createDrawer() {
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(true);
        menuLayout.setSpacing(false);
        menuLayout.getStyle()
            .set("background", "#f9fafb")
            .set("height", "100%");
        
        RouterLink tasksLink = new RouterLink("Tarefas", TaskView.class);
        tasksLink.setHighlightCondition((link, event) -> true);
        Icon tasksIcon = new Icon(VaadinIcon.CLIPBOARD_TEXT);
        
        HorizontalLayout tasksNav = new HorizontalLayout(tasksIcon, tasksLink);
        tasksNav.setAlignItems(FlexComponent.Alignment.CENTER);
        tasksNav.setSpacing(true);
        tasksNav.getStyle()
            .set("padding", "0.75rem 1rem")
            .set("border-radius", "8px")
            .set("background", "#667eea")
            .set("color", "white")
            .set("cursor", "pointer");
        
        menuLayout.add(tasksNav);
        addToDrawer(menuLayout);
    }
}
