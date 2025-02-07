package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue extends BaseEntity {
    @ManyToMany(mappedBy = "venues")
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String country;

    @Column(nullable = false, length = 255)
    private String city;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String website;

    @Column(length = 15)
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(getId(), venue.getId()) &&
                Objects.equals(name, venue.name) &&
                Objects.equals(country, venue.country) &&
                Objects.equals(city, venue.city) &&
                Objects.equals(address, venue.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, country, city, address);
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + getId() +
                "country=" + getCountry() +
                "city=" + getCity() +
                "address" + getAddress() +
                '}';
    }
}