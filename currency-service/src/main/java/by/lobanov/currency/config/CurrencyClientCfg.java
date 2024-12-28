package by.lobanov.currency.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "currency.client")
public class CurrencyClientCfg {
    private String url;
}
