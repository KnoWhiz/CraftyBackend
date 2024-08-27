package org.curastone.Crafty.controller;

import java.util.List;
import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Subscription;
import org.curastone.Crafty.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

  @Autowired private SubscriptionService subscriptionService;

  @PostMapping
  public ResponseEntity<?> createSubscription(@RequestBody Subscription subscription) {
    try {
      Subscription savedSubscription = subscriptionService.createSubscription(subscription);
      return ResponseEntity.ok(savedSubscription);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Subscription>> getSubscriptionsByUserId(
          @PathVariable ObjectId userId) {
    List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
    return ResponseEntity.ok(subscriptions);
  }
}
