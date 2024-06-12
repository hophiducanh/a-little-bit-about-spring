package com.logbasex.rabbitmq.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RabbitMqConst {
	public static final String FANDELO_EXCHANGE = "fandelo";
	public static final String ROUTER_FORMAT = "router.{queueName}";
	public static final String WHATSAPP_TALENT_QUEUE_NAME_FORMAT = "whatsapp.talent.{id}";
	public static final String WHATSAPP_PARTNER_CONTACT_QUEUE_NAME_FORMAT = "whatsapp.partner.contact.{id}";
	public static final String WHATSAPP_AGENCY_USER_QUEUE_NAME_FORMAT = "whatsapp.agency.user.{id}";
}
