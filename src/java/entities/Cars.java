/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author NnT
 */
@Entity
@Table(name = "Cars")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cars.findAll", query = "SELECT c FROM Cars c"),
    @NamedQuery(name = "Cars.findByCarID", query = "SELECT c FROM Cars c WHERE c.carID = :carID"),
    @NamedQuery(name = "Cars.findByCarName", query = "SELECT c FROM Cars c WHERE c.carName = :carName"),
    @NamedQuery(name = "Cars.findByCarType", query = "SELECT c FROM Cars c WHERE c.carType = :carType"),
    @NamedQuery(name = "Cars.findByEngine", query = "SELECT c FROM Cars c WHERE c.engine = :engine"),
    @NamedQuery(name = "Cars.findByPassengerCapacity", query = "SELECT c FROM Cars c WHERE c.passengerCapacity = :passengerCapacity"),
    @NamedQuery(name = "Cars.findByColor", query = "SELECT c FROM Cars c WHERE c.color = :color"),
    @NamedQuery(name = "Cars.findByImportDate", query = "SELECT c FROM Cars c WHERE c.importDate = :importDate"),
    @NamedQuery(name = "Cars.findByWarranty", query = "SELECT c FROM Cars c WHERE c.warranty = :warranty"),
    @NamedQuery(name = "Cars.findByImageURL", query = "SELECT c FROM Cars c WHERE c.imageURL = :imageURL"),
    @NamedQuery(name = "Cars.findByPrice", query = "SELECT c FROM Cars c WHERE c.price = :price"),
    @NamedQuery(name = "Cars.findByQuantity", query = "SELECT c FROM Cars c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "Cars.findByAvailabilityStatus", query = "SELECT c FROM Cars c WHERE c.availabilityStatus = :availabilityStatus")})
public class Cars implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "CarID")
    private String carID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "CarName")
    private String carName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CarType")
    private boolean carType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Engine")
    private String engine;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PassengerCapacity")
    private int passengerCapacity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "Color")
    private String color;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ImportDate")
    private String importDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Warranty")
    private int warranty;
    @Size(max = 200)
    @Column(name = "ImageURL")
    private String imageURL;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Price")
    private float price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Quantity")
    private int quantity;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AvailabilityStatus")
    private boolean availabilityStatus;
    @JoinColumn(name = "CarModel", referencedColumnName = "CarModel")
    @ManyToOne(optional = false)
    private Model carModel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carID")
    private List<Orders> ordersList;

    public Cars() {
    }

    public Cars(String carID) {
        this.carID = carID;
    }

    public Cars(String carID, String carName, boolean carType, String engine, int passengerCapacity, String color, String importDate, int warranty, float price, int quantity, boolean availabilityStatus) {
        this.carID = carID;
        this.carName = carName;
        this.carType = carType;
        this.engine = engine;
        this.passengerCapacity = passengerCapacity;
        this.color = color;
        this.importDate = importDate;
        this.warranty = warranty;
        this.price = price;
        this.quantity = quantity;
        this.availabilityStatus = availabilityStatus;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public boolean getCarType() {
        return carType;
    }

    public void setCarType(boolean carType) {
        this.carType = carType;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Model getCarModel() {
        return carModel;
    }

    public void setCarModel(Model carModel) {
        this.carModel = carModel;
    }

    @XmlTransient
    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carID != null ? carID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cars)) {
            return false;
        }
        Cars other = (Cars) object;
        if ((this.carID == null && other.carID != null) || (this.carID != null && !this.carID.equals(other.carID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Cars[ carID=" + carID + " ]";
    }
    
}
