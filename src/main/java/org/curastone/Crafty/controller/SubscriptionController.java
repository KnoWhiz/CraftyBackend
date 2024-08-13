package org.curastone.Crafty.controller;

import java.util.List;
import org.curastone.Crafty.dao.SubscriptionDao;
import org.curastone.Crafty.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
  @Autowired private SubscriptionDao subscriptionDao;

  @GetMapping
  public List<Subscription> getAllSubscriptions() {
    return subscriptionDao.findAll();
  }

  @PostMapping
  public Subscription createSubscription(@RequestBody Subscription subscription) {
    return subscriptionDao.save(subscription);
  }
}
