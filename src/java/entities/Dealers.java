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
@Table(name = "Dealers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dealers.findAll", query = "SELECT d FROM Dealers d"),
    @NamedQuery(name = "Dealers.findByDealerID", query = "SELECT d FROM Dealers d WHERE d.dealerID = :dealerID"),
    @NamedQuery(name = "Dealers.findByDealerName", query = "SELECT d FROM Dealers d WHERE d.dealerName = :dealerName"),
    @NamedQuery(name = "Dealers.findByAddress", query = "SELECT d FROM Dealers d WHERE d.address = :address"),
    @NamedQuery(name = "Dealers.findByPhoneNumber", query = "SELECT d FROM Dealers d WHERE d.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Dealers.findByAvailabilityStatus", query = "SELECT d FROM Dealers d WHERE d.availabilityStatus = :availabilityStatus")})
public class Dealers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "DealerID")
    private String dealerID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DealerName")
    private String dealerName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "Address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "PhoneNumber")
    private String phoneNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AvailabilityStatus")
    private boolean availabilityStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dealerID")
    private List<Orders> ordersList;

    public Dealers() {
    }

    public Dealers(String dealerID) {
        this.dealerID = dealerID;
    }

    public Dealers(String dealerID, String dealerName, String address, String phoneNumber, boolean availabilityStatus) {
        this.dealerID = dealerID;
        this.dealerName = dealerName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.availabilityStatus = availabilityStatus;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
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
        hash += (dealerID != null ? dealerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dealers)) {
            return false;
        }
        Dealers other = (Dealers) object;
        if ((this.dealerID == null && other.dealerID != null) || (this.dealerID != null && !this.dealerID.equals(other.dealerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Dealers[ dealerID=" + dealerID + " ]";
    }
    
}
