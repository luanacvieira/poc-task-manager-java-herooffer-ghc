package com.example.taskmanager.web.ui;

import com.example.taskmanager.domain.Category;
import com.example.taskmanager.domain.Priority;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value="tasks", layout = MainLayout.class)
@PageTitle("Tarefas")
@PermitAll
public class TaskView extends VerticalLayout {

    private final TaskService service;
    private final Grid<Task> grid = new Grid<>(Task.class, false);

    // Form fields
    private final TextField title = new TextField("Titulo");
    private final TextArea description = new TextArea("Descricao");
    private final ComboBox<Priority> priority = new ComboBox<>("Prioridade");
    private final ComboBox<Category> category = new ComboBox<>("Categoria");
    private final DatePicker dueDate = new DatePicker("Data Limite");
    private final TextField tags = new TextField("Tags (virgulas)");
    private final TextField assignedTo = new TextField("Atribuido a");
    private final Checkbox completed = new Checkbox("Concluida");

    public TaskView(TaskService service) {
        this.service = service;
        setSizeFull();
        buildForm();
        configureGrid();
        add(new H2("Gerenciamento de Tarefas"), buildStatsBar(), buildForm(), grid);
        refreshGrid();
    }

    private HorizontalLayout buildStatsBar() {
        long total = service.total();
        long pendingCount = service.pending();
        long completedCount = service.completed();
        long urgent = service.urgentActive();
        Div totalDiv = new Div(); totalDiv.setText("Total: " + total);
        Div pendingDiv = new Div(); pendingDiv.setText("Pendentes: " + pendingCount);
        Div completedDiv = new Div(); completedDiv.setText("Concluidas: " + completedCount);
        Div urgentDiv = new Div(); urgentDiv.setText("Urgentes: " + urgent);
        HorizontalLayout stats = new HorizontalLayout(totalDiv, pendingDiv, completedDiv, urgentDiv);
        stats.getStyle().set("gap","1rem");
        return stats;
    }

    private FormLayout buildForm() {
        priority.setItems(Priority.values());
        priority.setValue(Priority.MEDIUM);
        category.setItems(Category.values());
        category.setValue(Category.OTHER);
        dueDate.setValue(null);

        Button save = new Button("Salvar", e -> save());
        Button clear = new Button("Limpar", e -> clearForm());
        FormLayout form = new FormLayout(title, description, priority, category, dueDate, tags, assignedTo, completed, new HorizontalLayout(save, clear));
        form.setColspan(description, 2);
        return form;
    }

    private void configureGrid() {
        grid.addColumn(Task::getId).setHeader("ID");
        grid.addColumn(Task::getTitle).setHeader("Titulo").setAutoWidth(true);
        grid.addColumn(t -> t.getPriority().name()).setHeader("Prioridade");
        grid.addColumn(t -> t.getCategory().name()).setHeader("Categoria");
        grid.addColumn(t -> t.getDueDate() != null ? t.getDueDate().toString() : "-").setHeader("Vencimento");
        grid.addColumn(t -> t.getAssignedTo() != null ? t.getAssignedTo() : "-").setHeader("Responsavel");
        grid.addColumn(t -> t.isCompleted() ? "Sim" : "Nao").setHeader("Status");
        grid.addColumn(t -> t.getTags() != null ? String.join(",", t.getTags()) : "").setHeader("Tags");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("500px");

        grid.addItemClickListener(evt -> {
            Task t = evt.getItem();
            populateForm(t);
        });
    }

    private void populateForm(Task t) {
        title.setValue(t.getTitle());
        description.setValue(t.getDescription() != null ? t.getDescription() : "");
        priority.setValue(t.getPriority());
        category.setValue(t.getCategory());
        dueDate.setValue(t.getDueDate());
        tags.setValue(t.getTags() != null ? String.join(",", t.getTags()) : "");
        assignedTo.setValue(t.getAssignedTo() != null ? t.getAssignedTo() : "");
        completed.setValue(t.isCompleted());
    }

    private void clearForm() {
        title.clear();
        description.clear();
        priority.setValue(Priority.MEDIUM);
        category.setValue(Category.OTHER);
        dueDate.clear();
        tags.clear();
        assignedTo.clear();
        completed.clear();
    }

    private void save() {
        if (title.isEmpty()) {
            Notification.show("Titulo e obrigatorio", 3000, Notification.Position.MIDDLE);
            return;
        }
        Task task = new Task();
        task.setTitle(title.getValue());
        task.setDescription(description.getValue());
        task.setPriority(priority.getValue());
        task.setCategory(category.getValue());
        task.setDueDate(dueDate.getValue());
        task.setAssignedTo(assignedTo.getValue());
        task.setUserId("user1"); // simplificacao
        task.setCompleted(completed.getValue());
        if (!tags.isEmpty()) {
            Set<String> tagSet = Arrays.stream(tags.getValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toCollection(HashSet::new));
            task.setTags(tagSet);
        }
        service.create(task);
        clearForm();
        refreshGrid();
        Notification.show("Tarefa salva com sucesso!", 2000, Notification.Position.BOTTOM_START);
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
        grid.getDataProvider().refreshAll();
    }
}
