package com.adharsh.adharshmart.service.chat;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Canned FAQ answers, no network call — the default provider so the chatbot works out of the box
 * without an API key, and the guaranteed fallback the real provider degrades to on failure.
 * Covers the minimum 5-10 FAQ-style domain questions required for the Final Review.
 */
public class MockChatProvider implements ChatProvider {

    private static final Map<String, String> FAQS = new LinkedHashMap<>();

    static {
        FAQS.put("shipping", "Standard shipping takes 3-5 business days; express options appear at checkout.");
        FAQS.put("return", "Items can be returned within 14 days of delivery in original condition for a full refund.");
        FAQS.put("refund", "Refunds are issued to your original payment method within 5-7 business days of us receiving the return.");
        FAQS.put("size", "Each product page lists a size guide under the description — sizing runs true to standard EU/US charts.");
        FAQS.put("track", "Track any order from Order History once it ships — you'll see status move from Confirmed to Shipped to Delivered.");
        FAQS.put("payment", "Checkout uses a secure mock payment confirmation for this build; no real card details are processed.");
        FAQS.put("seller", "Anyone can apply to sell by registering with a Seller account and listing products from their dashboard.");
        FAQS.put("cancel", "Orders can be cancelled while still Pending or Confirmed — contact support once an item has shipped.");
        FAQS.put("stock", "Stock levels update live; if an item shows out of stock it is temporarily unavailable for purchase.");
        FAQS.put("review", "You can leave a star rating and review once an order for that product is marked Delivered.");
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.isBlank()) {
            return defaultReply();
        }
        String lower = userMessage.toLowerCase();
        for (Map.Entry<String, String> entry : FAQS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return defaultReply();
    }

    private String defaultReply() {
        return "I can help with shipping, returns, sizing, tracking, payments, and seller questions for "
                + "AdharshMart — could you rephrase your question around one of those topics?";
    }
}
