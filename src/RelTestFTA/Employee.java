/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;

/**
 *
 * @author jiewdue
 */
public class Employee{
    String id;
    String firstName;
    String lastName;
    String location;
    @Override
    public String toString() {
      return firstName+" "+lastName+"("+id+")"+location;
    }
}
