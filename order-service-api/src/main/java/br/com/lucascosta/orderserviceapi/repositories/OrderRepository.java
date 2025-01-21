package br.com.lucascosta.orderserviceapi.repositories;

import br.com.lucascosta.orderserviceapi.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
