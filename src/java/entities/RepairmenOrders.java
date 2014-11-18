/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author NnT
 */
@Entity
@Table(name = "RepairmenOrders")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RepairmenOrders.findAll", query = "SELECT r FROM RepairmenOrders r"),
    @NamedQuery(name = "RepairmenOrders.findByRepCarID", query = "SELECT r FROM RepairmenOrders r WHERE r.repCarID = :repCarID"),
    @NamedQuery(name = "RepairmenOrders.findByOrderID", query = "SELECT r FROM RepairmenOrders r WHERE r.orderID = :orderID"),
    @NamedQuery(name = "RepairmenOrders.findByErrors", query = "SELECT r FROM RepairmenOrders r WHERE r.errors = :errors"),
    @NamedQuery(name = "RepairmenOrders.findByRepairDate", query = "SELECT r FROM RepairmenOrders r WHERE r.repairDate = :repairDate"),
    @NamedQuery(name = "RepairmenOrders.findByFixedDate", query = "SELECT r FROM RepairmenOrders r WHERE r.fixedDate = :fixedDate"),
    @NamedQuery(name = "RepairmenOrders.findByFee", query = "SELECT r FROM RepairmenOrders r WHERE r.fee = :fee"),
    @NamedQuery(name = "RepairmenOrders.findByRepairStatus", query = "SELECT r FROM RepairmenOrders r WHERE r.repairStatus = :repairStatus")})
public class RepairmenOrders implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "RepCarID")
    private String repCarID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "OrderID")
    private String orderID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "Errors")
    private String errors;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "RepairDate")
    private String repairDate;
    @Size(max = 10)
    @Column(name = "FixedDate")
    private String fixedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Fee")
    private float fee;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "RepairStatus")
    private String repairStatus;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "repairmenOrders")
    private Orders orders;

    public RepairmenOrders() {
    }

    public RepairmenOrders(String repCarID) {
        this.repCarID = repCarID;
    }

    public RepairmenOrders(String repCarID, String orderID, String errors, String repairDate, float fee, String repairStatus) {
        this.repCarID = repCarID;
        this.orderID = orderID;
        this.errors = errors;
        this.repairDate = repairDate;
        this.fee = fee;
        this.repairStatus = repairStatus;
    }

    public String getRepCarID() {
        return repCarID;
    }

    public void setRepCarID(String repCarID) {
        this.repCarID = repCarID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(String repairDate) {
        this.repairDate = repairDate;
    }

    public String getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(String fixedDate) {
        this.fixedDate = fixedDate;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (repCarID != null ? repCarID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RepairmenOrders)) {
            return false;
        }
        RepairmenOrders other = (RepairmenOrders) object;
        if ((this.repCarID == null && other.repCarID != null) || (this.repCarID != null && !this.repCarID.equals(other.repCarID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RepairmenOrders[ repCarID=" + repCarID + " ]";
    }
    
}
