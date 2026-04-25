package org.example.common.models;

import org.example.common.util.Validator;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Organization implements Comparable<Organization>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private float annualTurnover;
    private OrganizationType type;
    private Address officialAddress;
    private Long ownerId;


    public Organization() {
    }

    public Organization(String name, Coordinates coordinates, float annualTurnover, OrganizationType type, Address officialAddress){
        Validator.validateName(name);
        Validator.validateAnnualTurnover(annualTurnover);
        Validator.validateType(type);
        Validator.validateCoordinates(coordinates);

        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    public long getId() {
        return id;
    }

    public OrganizationType getType(){
        return type;
    }

    public Address getOfficialAddress(){
        return officialAddress;
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }


    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public float getAnnualTurnover() {
        return annualTurnover;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setAnnualTurnover(float annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public void setOfficialAddress(Address officialAddress) {
        this.officialAddress = officialAddress;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public int compareTo(Organization o) {
        return Long.compare(this.id, o.id);
    }

    public Organization(long id, LocalDate creationDate, String name, Coordinates coordinates,
                        float annualTurnover, OrganizationType type, Address officialAddress) {
        Validator.validateName(name);
        Validator.validateAnnualTurnover(annualTurnover);
        Validator.validateType(type);
        Validator.validateCoordinates(coordinates);

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return id == that.id && Float.compare(annualTurnover, that.annualTurnover) == 0 && Objects.equals(name, that.name)
                && Objects.equals(coordinates, that.coordinates) && Objects.equals(creationDate, that.creationDate)
                && type == that.type && Objects.equals(officialAddress, that.officialAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, annualTurnover, type, officialAddress);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates = " + "{X = " + coordinates.getX() + "; Y = " + coordinates.getY() + "}" +
                ", creationDate = " + creationDate +
                ", annualTurnover = " + annualTurnover +
                ", type = " + type +
                ", officialAddress = " + officialAddress +
                '}';
    }
}

