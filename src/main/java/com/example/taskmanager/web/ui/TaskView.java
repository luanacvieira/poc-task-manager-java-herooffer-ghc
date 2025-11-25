package com.example.taskmanager.web.ui;

import com.example.taskmanager.domain.Category;
import com.example.taskmanager.domain.Priority;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
@PageTitle("Tarefas | Task Manager")
@PermitAll
public class TaskView extends VerticalLayout {

    private final TaskService service;
    private final Grid<Task> grid = new Grid<>(Task.class, false);

    // Form fields
    private final TextField title = new TextField("Título");
    private final TextArea description = new TextArea("Descrição");
    private final ComboBox<Priority> priority = new ComboBox<>("Prioridade");
    private final ComboBox<Category> category = new ComboBox<>("Categoria");
    private final DatePicker dueDate = new DatePicker("Data de Vencimento");
    private final TextField tags = new TextField("Tags");
    private final TextField assignedTo = new TextField("Atribuído a");
    private final Checkbox completed = new Checkbox("Concluída");
    
    // Filter fields
    private final ComboBox<Priority> filterPriority = new ComboBox<>("Filtrar por Prioridade");
    private final ComboBox<Category> filterCategory = new ComboBox<>("Filtrar por Categoria");
    private final ComboBox<String> filterStatus = new ComboBox<>("Filtrar por Status");
    
    private Task selectedTask = null;
    private final Div statsContainer = new Div();

    public TaskView(TaskService service) {
        this.service = service;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("min-height", "100vh");
        
        add(buildHeader(), buildStatsBar(), buildFilterBar(), buildTaskCards());
        refreshView();
    }

    private Div buildHeader() {
        H2 pageTitle = new H2("📋 Gerenciamento de Tarefas");
        pageTitle.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-weight", "600");
        
        Button newTaskBtn = new Button("Nova Tarefa", new Icon(VaadinIcon.PLUS));
        newTaskBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        newTaskBtn.getStyle()
            .set("background", "#10b981")
            .set("color", "white");
        newTaskBtn.addClickListener(e -> openTaskDialog(null));
        
        HorizontalLayout header = new HorizontalLayout(pageTitle, newTaskBtn);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
            .set("padding", "1.5rem")
            .set("background", "rgba(255, 255, 255, 0.1)")
            .set("border-radius", "12px")
            .set("backdrop-filter", "blur(10px)");
        
        Div headerContainer = new Div(header);
        headerContainer.setWidthFull();
        return headerContainer;
    }
    
    private HorizontalLayout buildStatsBar() {
        statsContainer.setWidthFull();
        statsContainer.getStyle()
            .set("display", "flex")
            .set("gap", "1rem")
            .set("flex-wrap", "wrap");
        
        updateStats();
        
        HorizontalLayout statsBar = new HorizontalLayout(statsContainer);
        statsBar.setWidthFull();
        statsBar.setPadding(false);
        return statsBar;
    }
    
    private void updateStats() {
        statsContainer.removeAll();
        
        long total = service.total();
        long pendingCount = service.pending();
        long completedCount = service.completed();
        long urgent = service.urgentActive();
        
        statsContainer.add(
            createStatCard("Total", String.valueOf(total), VaadinIcon.CLIPBOARD_TEXT, "#3b82f6"),
            createStatCard("Pendentes", String.valueOf(pendingCount), VaadinIcon.CLOCK, "#f59e0b"),
            createStatCard("Concluídas", String.valueOf(completedCount), VaadinIcon.CHECK_CIRCLE, "#10b981"),
            createStatCard("Urgentes", String.valueOf(urgent), VaadinIcon.WARNING, "#ef4444")
        );
    }
    
    private Div createStatCard(String label, String value, VaadinIcon icon, String color) {
        Icon iconComponent = new Icon(icon);
        iconComponent.getStyle()
            .set("color", color)
            .set("width", "2.5rem")
            .set("height", "2.5rem");
        
        Span valueSpan = new Span(value);
        valueSpan.getStyle()
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1f2937");
        
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
            .set("font-size", "0.875rem")
            .set("color", "#6b7280")
            .set("text-transform", "uppercase")
            .set("letter-spacing", "0.05em");
        
        VerticalLayout textLayout = new VerticalLayout(valueSpan, labelSpan);
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        
        HorizontalLayout content = new HorizontalLayout(iconComponent, textLayout);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);
        
        Div card = new Div(content);
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1.5rem")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("flex", "1")
            .set("min-width", "200px");
        
        return card;
    }

    private HorizontalLayout buildFilterBar() {
        filterPriority.setItems(Priority.values());
        filterPriority.setPlaceholder("Todas as prioridades");
        filterPriority.setClearButtonVisible(true);
        filterPriority.addValueChangeListener(e -> applyFilters());
        
        filterCategory.setItems(Category.values());
        filterCategory.setPlaceholder("Todas as categorias");
        filterCategory.setClearButtonVisible(true);
        filterCategory.addValueChangeListener(e -> applyFilters());
        
        filterStatus.setItems("Todas", "Pendentes", "Concluídas");
        filterStatus.setValue("Todas");
        filterStatus.addValueChangeListener(e -> applyFilters());
        
        Button clearFilters = new Button("Limpar Filtros", new Icon(VaadinIcon.REFRESH));
        clearFilters.addClickListener(e -> clearFilters());
        
        HorizontalLayout filters = new HorizontalLayout(filterPriority, filterCategory, filterStatus, clearFilters);
        filters.setWidthFull();
        filters.setAlignItems(FlexComponent.Alignment.END);
        filters.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1rem")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)");
        
        return filters;
    }
    
    private void applyFilters() {
        var tasks = service.findAll();
        
        if (filterPriority.getValue() != null) {
            tasks = tasks.stream()
                .filter(t -> t.getPriority() == filterPriority.getValue())
                .collect(Collectors.toList());
        }
        
        if (filterCategory.getValue() != null) {
            tasks = tasks.stream()
                .filter(t -> t.getCategory() == filterCategory.getValue())
                .collect(Collectors.toList());
        }
        
        if (filterStatus.getValue() != null) {
            switch (filterStatus.getValue()) {
                case "Pendentes":
                    tasks = tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
                    break;
                case "Concluídas":
                    tasks = tasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
                    break;
            }
        }
        
        updateTaskCards(tasks);
    }
    
    private void clearFilters() {
        filterPriority.clear();
        filterCategory.clear();
        filterStatus.setValue("Todas");
        refreshView();
    }

    private Div tasksCardsContainer = new Div();
    
    private VerticalLayout buildTaskCards() {
        tasksCardsContainer.setWidthFull();
        tasksCardsContainer.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(350px, 1fr))")
            .set("gap", "1.5rem")
            .set("padding", "0.5rem");
        
        VerticalLayout container = new VerticalLayout(tasksCardsContainer);
        container.setWidthFull();
        container.setPadding(false);
        container.setSpacing(false);
        
        return container;
    }
    
    private void updateTaskCards(java.util.List<Task> tasks) {
        tasksCardsContainer.removeAll();
        
        if (tasks.isEmpty()) {
            Div emptyState = new Div();
            emptyState.setText("📭 Nenhuma tarefa encontrada");
            emptyState.getStyle()
                .set("text-align", "center")
                .set("padding", "3rem")
                .set("color", "white")
                .set("font-size", "1.25rem");
            tasksCardsContainer.add(emptyState);
            return;
        }
        
        tasks.forEach(task -> tasksCardsContainer.add(createTaskCard(task)));
    }
    
    private Div createTaskCard(Task task) {
        // Priority badge
        Span priorityBadge = new Span(getPriorityIcon(task.getPriority()) + " " + getPriorityLabel(task.getPriority()));
        priorityBadge.getStyle()
            .set("background", getPriorityColor(task.getPriority()))
            .set("color", "white")
            .set("padding", "0.25rem 0.75rem")
            .set("border-radius", "9999px")
            .set("font-size", "0.75rem")
            .set("font-weight", "600");
        
        // Category badge
        Span categoryBadge = new Span(getCategoryIcon(task.getCategory()) + " " + getCategoryLabel(task.getCategory()));
        categoryBadge.getStyle()
            .set("background", "#6366f1")
            .set("color", "white")
            .set("padding", "0.25rem 0.75rem")
            .set("border-radius", "9999px")
            .set("font-size", "0.75rem")
            .set("font-weight", "600");
        
        HorizontalLayout badges = new HorizontalLayout(priorityBadge, categoryBadge);
        badges.setSpacing(true);
        badges.setPadding(false);
        
        // Title
        H3 titleElement = new H3(task.getTitle());
        titleElement.getStyle()
            .set("margin", "0.75rem 0 0.5rem 0")
            .set("color", "#1f2937")
            .set("font-size", "1.25rem");
        if (task.isCompleted()) {
            titleElement.getStyle()
                .set("text-decoration", "line-through")
                .set("color", "#9ca3af");
        }
        
        // Description
        Span descSpan = new Span(task.getDescription() != null && !task.getDescription().isBlank() 
            ? task.getDescription() 
            : "Sem descrição");
        descSpan.getStyle()
            .set("color", "#6b7280")
            .set("font-size", "0.875rem")
            .set("display", "block")
            .set("margin-bottom", "1rem");
        
        // Task info
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(false);
        infoLayout.setSpacing(false);
        infoLayout.getStyle().set("gap", "0.5rem");
        
        if (task.getDueDate() != null) {
            infoLayout.add(createInfoRow("📅", "Vencimento: " + task.getDueDate()));
        }
        if (task.getAssignedTo() != null && !task.getAssignedTo().isBlank()) {
            infoLayout.add(createInfoRow("👤", "Responsável: " + task.getAssignedTo()));
        }
        if (task.getTags() != null && !task.getTags().isEmpty()) {
            infoLayout.add(createInfoRow("🏷️", "Tags: " + String.join(", ", task.getTags())));
        }
        
        // Status
        Span statusBadge = new Span(task.isCompleted() ? "✅ Concluída" : "⏳ Pendente");
        statusBadge.getStyle()
            .set("background", task.isCompleted() ? "#d1fae5" : "#fef3c7")
            .set("color", task.isCompleted() ? "#065f46" : "#92400e")
            .set("padding", "0.5rem 1rem")
            .set("border-radius", "6px")
            .set("font-size", "0.875rem")
            .set("font-weight", "600")
            .set("display", "inline-block")
            .set("margin-top", "0.75rem");
        
        // Action buttons
        Button editBtn = new Button("Editar", new Icon(VaadinIcon.EDIT));
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        editBtn.addClickListener(e -> openTaskDialog(task));
        
        Button deleteBtn = new Button("Excluir", new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteBtn.addClickListener(e -> deleteTask(task));
        
        Button toggleBtn = new Button(task.isCompleted() ? "Reabrir" : "Concluir", 
            new Icon(task.isCompleted() ? VaadinIcon.ARROW_BACKWARD : VaadinIcon.CHECK));
        toggleBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toggleBtn.addClickListener(e -> toggleTaskStatus(task));
        
        HorizontalLayout actions = new HorizontalLayout(editBtn, toggleBtn, deleteBtn);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("margin-top", "1rem");
        
        // Card assembly
        VerticalLayout cardContent = new VerticalLayout(badges, titleElement, descSpan, infoLayout, statusBadge, actions);
        cardContent.setPadding(false);
        cardContent.setSpacing(false);
        
        Div card = new Div(cardContent);
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1.5rem")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("transition", "transform 0.2s, box-shadow 0.2s")
            .set("cursor", "pointer");
        
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-4px)")
                .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.2)");
        });
        
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)");
        });
        
        return card;
    }
    
    private Div createInfoRow(String icon, String text) {
        Span span = new Span(icon + " " + text);
        span.getStyle()
            .set("font-size", "0.875rem")
            .set("color", "#4b5563");
        
        Div row = new Div(span);
        return row;
    }

    private void openTaskDialog(Task task) {
        selectedTask = task;
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeaderTitle(task == null ? "Nova Tarefa" : "Editar Tarefa");
        
        // Configure form fields
        title.setRequired(true);
        title.setRequiredIndicatorVisible(true);
        title.setPlaceholder("Digite o título da tarefa");
        title.setPrefixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        
        description.setPlaceholder("Descreva os detalhes da tarefa");
        description.setHeight("120px");
        
        priority.setItems(Priority.values());
        priority.setValue(task != null ? task.getPriority() : Priority.MEDIUM);
        priority.setItemLabelGenerator(p -> getPriorityIcon(p) + " " + getPriorityLabel(p));
        
        category.setItems(Category.values());
        category.setValue(task != null ? task.getCategory() : Category.OTHER);
        category.setItemLabelGenerator(c -> getCategoryIcon(c) + " " + getCategoryLabel(c));
        
        dueDate.setPlaceholder("Selecione a data");
        
        tags.setPlaceholder("Ex: urgente, cliente, revisão");
        tags.setHelperText("Separe as tags com vírgula");
        tags.setPrefixComponent(new Icon(VaadinIcon.TAG));
        
        assignedTo.setPlaceholder("Nome do responsável");
        assignedTo.setPrefixComponent(new Icon(VaadinIcon.USER));
        
        // Populate form if editing
        if (task != null) {
            title.setValue(task.getTitle());
            description.setValue(task.getDescription() != null ? task.getDescription() : "");
            priority.setValue(task.getPriority());
            category.setValue(task.getCategory());
            dueDate.setValue(task.getDueDate());
            tags.setValue(task.getTags() != null ? String.join(", ", task.getTags()) : "");
            assignedTo.setValue(task.getAssignedTo() != null ? task.getAssignedTo() : "");
            completed.setValue(task.isCompleted());
        } else {
            clearForm();
        }
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(title, description, priority, category, dueDate, tags, assignedTo, completed);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(title, 2);
        formLayout.setColspan(description, 2);
        formLayout.setColspan(tags, 2);
        
        VerticalLayout dialogLayout = new VerticalLayout(formLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialog.add(dialogLayout);
        
        // Dialog buttons
        Button saveBtn = new Button("Salvar", new Icon(VaadinIcon.CHECK));
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> {
            if (saveTask()) {
                dialog.close();
                showSuccessNotification(task == null ? "Tarefa criada com sucesso!" : "Tarefa atualizada com sucesso!");
            }
        });
        
        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void clearForm() {
        title.clear();
        description.clear();
        priority.setValue(Priority.MEDIUM);
        category.setValue(Category.OTHER);
        dueDate.clear();
        tags.clear();
        assignedTo.clear();
        completed.setValue(false);
        selectedTask = null;
    }

    private boolean saveTask() {
        if (title.isEmpty()) {
            showErrorNotification("O título é obrigatório!");
            return false;
        }
        
        try {
            Task task = selectedTask != null ? selectedTask : new Task();
            task.setTitle(title.getValue());
            task.setDescription(description.getValue());
            task.setPriority(priority.getValue());
            task.setCategory(category.getValue());
            task.setDueDate(dueDate.getValue());
            task.setAssignedTo(assignedTo.getValue());
            task.setUserId("user1"); // simplificação
            task.setCompleted(completed.getValue());
            
            if (!tags.isEmpty()) {
                Set<String> tagSet = Arrays.stream(tags.getValue().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toCollection(HashSet::new));
                task.setTags(tagSet);
            } else {
                task.setTags(new HashSet<>());
            }
            
            if (selectedTask != null) {
                service.update(task.getId(), task);
            } else {
                service.create(task);
            }
            
            clearForm();
            refreshView();
            return true;
        } catch (Exception e) {
            showErrorNotification("Erro ao salvar tarefa: " + e.getMessage());
            return false;
        }
    }
    
    private void deleteTask(Task task) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Exclusão");
        
        Span message = new Span("Tem certeza que deseja excluir a tarefa \"" + task.getTitle() + "\"?");
        confirmDialog.add(message);
        
        Button confirmBtn = new Button("Excluir", e -> {
            try {
                service.delete(task.getId());
                confirmDialog.close();
                showSuccessNotification("Tarefa excluída com sucesso!");
                refreshView();
            } catch (Exception ex) {
                showErrorNotification("Erro ao excluir tarefa: " + ex.getMessage());
            }
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        
        Button cancelBtn = new Button("Cancelar", e -> confirmDialog.close());
        
        confirmDialog.getFooter().add(cancelBtn, confirmBtn);
        confirmDialog.open();
    }
    
    private void toggleTaskStatus(Task task) {
        try {
            task.setCompleted(!task.isCompleted());
            service.update(task.getId(), task);
            showSuccessNotification("Status atualizado!");
            refreshView();
        } catch (Exception e) {
            showErrorNotification("Erro ao atualizar status: " + e.getMessage());
        }
    }

    private void refreshView() {
        updateStats();
        updateTaskCards(service.findAll());
    }
    
    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    
    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
    // Helper methods for icons and labels
    private String getPriorityIcon(Priority p) {
        return switch (p) {
            case LOW -> "🟢";
            case MEDIUM -> "🟡";
            case HIGH -> "🟠";
            case URGENT -> "🔴";
        };
    }
    
    private String getPriorityLabel(Priority p) {
        return switch (p) {
            case LOW -> "Baixa";
            case MEDIUM -> "Média";
            case HIGH -> "Alta";
            case URGENT -> "Urgente";
        };
    }
    
    private String getPriorityColor(Priority p) {
        return switch (p) {
            case LOW -> "#10b981";
            case MEDIUM -> "#f59e0b";
            case HIGH -> "#f97316";
            case URGENT -> "#ef4444";
        };
    }
    
    private String getCategoryIcon(Category c) {
        return switch (c) {
            case WORK -> "💼";
            case PERSONAL -> "🏠";
            case STUDY -> "📚";
            case HEALTH -> "💪";
            case OTHER -> "📌";
        };
    }
    
    private String getCategoryLabel(Category c) {
        return switch (c) {
            case WORK -> "Trabalho";
            case PERSONAL -> "Pessoal";
            case STUDY -> "Estudos";
            case HEALTH -> "Saúde";
            case OTHER -> "Outros";
        };
    }
}
