package com.example.taskservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade de domínio representando uma Tarefa.
 * 
 * Esta classe encapsula todas as informações relacionadas a uma tarefa,
 * incluindo metadados, prioridade, categoria e tags.
 * 
 * Padrão: Aggregate Root no contexto de DDD (Domain-Driven Design)
 */
@Entity
@Table(name = "tasks")
@Getter 
@Setter 
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    /**
     * Identificador único da tarefa.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título da tarefa (obrigatório).
     * Representa uma descrição curta e objetiva.
     * 
     * Validações de segurança:
     * - Tamanho mínimo: 3 caracteres
     * - Tamanho máximo: 255 caracteres
     * - Padrão: apenas letras, números, espaços e pontuação básica
     */
    @NotBlank(message = "O título da tarefa é obrigatório")
    @Size(min = 3, max = 255, message = "Título deve ter entre 3 e 255 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?\\-:()]+$", message = "Título contém caracteres inválidos")
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Descrição detalhada da tarefa (opcional).
     * 
     * Validações de segurança:
     * - Tamanho máximo: 1000 caracteres
     */
    @Size(max = 1000, message = "Descrição não pode exceder 1000 caracteres")
    @Column(length = 1000)
    private String description;

    /**
     * Nível de prioridade da tarefa.
     * Valores: LOW, MEDIUM, HIGH, URGENT
     */
    @NotNull(message = "A prioridade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    /**
     * Categoria da tarefa para organização.
     * Valores: WORK, PERSONAL, STUDY, HEALTH, OTHER
     */
    @NotNull(message = "A categoria é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category = Category.OTHER;

    /**
     * Data de vencimento da tarefa (opcional).
     * Armazenada sem timezone para simplicidade.
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Tags associadas à tarefa para busca e filtro.
     * Relacionamento many-to-many simplificado.
     * 
     * Validações de segurança:
     * - Máximo: 10 tags por tarefa (previne abuso)
     * - Formato: apenas lowercase, números e hífens
     * - Tamanho: 2-20 caracteres por tag
     */
    @Size(max = 10, message = "Máximo de 10 tags permitidas por tarefa")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag", length = 20)
    private Set<@Pattern(regexp = "^[a-z0-9-]{2,20}$", message = "Tag deve conter apenas letras minúsculas, números e hífens (2-20 caracteres)") String> tags = new HashSet<>();

    /**
     * Usuário ou pessoa para quem a tarefa foi atribuída (opcional).
     * 
     * Validações de segurança:
     * - Tamanho máximo: 50 caracteres
     * - Formato: apenas alfanuméricos, hífens e underscores
     */
    @Size(max = 50, message = "Campo assignedTo não pode exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "AssignedTo deve conter apenas letras, números, hífens e underscores")
    @Column(name = "assigned_to", length = 50)
    private String assignedTo;

    /**
     * ID do usuário proprietário da tarefa.
     * Em uma arquitetura de microsserviços, este seria uma referência
     * externa para o serviço de usuários.
     * 
     * Validações de segurança:
     * - Tamanho: 3-50 caracteres
     * - Formato: apenas alfanuméricos, hífens e underscores
     */
    @NotBlank(message = "O userId é obrigatório")
    @Size(min = 3, max = 50, message = "UserId deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "UserId deve conter apenas letras, números, hífens e underscores")
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    /**
     * Indica se a tarefa foi concluída.
     */
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Data/hora de criação da tarefa.
     * Preenchida automaticamente pelo Hibernate.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data/hora da última atualização.
     * Atualizada automaticamente pelo Hibernate.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
