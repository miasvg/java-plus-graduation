package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

//подборки мероприятий

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    //этот флаг указывает выводить подборку на главный экран или нет, true = выводить
    @Column(name = "pinned")
    Boolean pinned;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compilation that = (Compilation) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(pinned, that.pinned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, pinned);
    }
}
