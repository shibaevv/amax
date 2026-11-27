package au.com.totemsoft.amax;

import java.nio.CharBuffer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.locks.LockSupport;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import au.com.totemsoft.amax.dao.RuleEventDao;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.rule.ExposureKeyProcessor;
import au.com.totemsoft.amax.rule.RulePostProcessor;
import au.com.totemsoft.amax.rule.RuleProcessor;
import au.com.totemsoft.amax.rule.SerieDataProcessor;
import au.com.totemsoft.amax.tool.ExposureKeyDisruptor;
import au.com.totemsoft.amax.tool.RuleDisruptor;
import au.com.totemsoft.amax.tool.RuleEventProducer;
import au.com.totemsoft.amax.tool.SeriesDataDisruptor;
import lombok.extern.log4j.Log4j2;

//
// mvn clean test -Dtest=au.com.totemsoft.amax.RuleEventTest
//
@SpringJUnitConfig(locations = {"classpath:spring/ruleEvent.test.xml"})
@Sql(scripts = "classpath:ruleEvent.sql")
//@ActiveProfiles(profiles = "")
@Log4j2
class RuleEventTest {

    @Value("${db.url}")
    private String dbUrl;

    @Autowired private DataSource dataSource;

    @Autowired private RuleEventDao ruleEventDao;

    @Autowired private JournalProcessor<RuleEvent> journalRuleProcessor;
    @Autowired private JournalProcessor<ExposureKeyEvent> journalExposureKeyProcessor;
    @Autowired private JournalProcessor<SeriesDataEvent> journalSerieDataProcessor;

    @Autowired private RuleEventProducer ruleProducer;

    @Autowired private RuleDisruptor ruleDisruptor;
    @Autowired private RuleProcessor ruleProcessor;

    @Autowired private ExposureKeyDisruptor exposureKeyDisruptor;
    @Autowired private ExposureKeyProcessor exposureKeyProcessor;

    @Autowired private SeriesDataDisruptor seriesDataDisruptor;
    @Autowired private SerieDataProcessor seriesDataProcessor;

    @Autowired private RuleDisruptor outputRuleDisruptor;
    @Autowired private RulePostProcessor outputRuleProcessor;

    private Context context;

    private Date latestDate() {
        final LocalDate date = LocalDate.now().minusDays(1);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void before() {
        log.info("dbUrl: {}", dbUrl);

        // Connect the handler(s)
        ruleDisruptor
            .handleEventsWith(journalRuleProcessor)
            //.then(replicatorProcessor)
            //.then(unmarshallerProcessor)
            .then(ruleProcessor)
            ;

        exposureKeyDisruptor
            .handleEventsWith(journalExposureKeyProcessor)
            .then(exposureKeyProcessor)
            ;
 
        seriesDataDisruptor
            .handleEventsWith(journalSerieDataProcessor)
            .then(seriesDataProcessor)
            ;

        outputRuleDisruptor
            .handleEventsWith(journalRuleProcessor)
            //.then(marshallerProcessor)
            .then(outputRuleProcessor)
            ;

        // Start the disruptor(s), starts all threads running (start in reverse order ???)
        outputRuleDisruptor.start();
        seriesDataDisruptor.start();
        exposureKeyDisruptor.start();
        ruleDisruptor.start();
    }

    @Test
    public final void test() throws Exception {
        context = Context.builder()
            .date(latestDate())
            .build();

        runRule(1L); // #SUM( #I1, #I2 )

        //runRule(6035L); // #MULT( #TMPL( #T1 ), 0.5 )
        //runRule(6036L); // #MULT( #TMPL( #T1 ), 0.5 )
        //runRule(6039L); // #AVR( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(6040L); // #AVR( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(1132L); // #WSTOF( 1, #TMPL( #T1 ), #TMPL( #T2 ), #TMPL( #T3 ), #TMPL( #T4 ), #TMPL( #T5 ), #TMPL( #T6 ), #TMPL( #T7 ), #TMPL( #T8 ), #TMPL( #T9 ), #TMPL( #T10 ) )
        //runRule(1145L); // #WSTOF( 1, #TMPL( #T1 ), #TMPL( #T2 ), #TMPL( #T3 ), #TMPL( #T4 ), #TMPL( #T5 ), #TMPL( #T6 ), #TMPL( #T7 ), #TMPL( #T8 ), #TMPL( #T9 ), #TMPL( #T10 ) )
        //runRule(7372L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(7371L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(3159L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(3160L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(3173L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(7002L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(7000L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(7001L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )

        // random rule(s)
        //runRule(2000L); // #SUM( #INCL( #W1 ) )
        //runRule(7000L); // #SUM( #TMPL( #T1 ), #TMPL( #T2 ) )
        //runRule(4457L); // #SUM( #I1 )
        //runRule(6797L); // #SUM( #I1, #I2, #I3 )
        //runRule(6035L); // #MULT( #TMPL( #T1 ), 0.5 )
        //runRule(5689L); // #DIVD( #AVR( #TMPL( #T1 ), #MULT( #TMPL( #T2 ), -1 ) ), 0.01 )
        //runRule(1132L); // #WSTOF( 1, #TMPL( #T1 ), #TMPL( #T2 ), #TMPL( #T3 ), #TMPL( #T4 ), #TMPL( #T5 ), #TMPL( #T6 ), #TMPL( #T7 ), #TMPL( #T8 ), #TMPL( #T9 ), #TMPL( #T10 ) )
        //runRule(7354L); // #WSTOF( 5, #INCL( #W1, #EXCL( #W2, #W3 ), #W4 ) )
        //runRule(5040L); // #ITR( #TMPL( #T1 ), #SUM( #INCL( #W2 ) ) )

        // wait until rule processing finish (or debug activities)
        System.in.read();
    }

    // feed rules one by one
    private void runRule(long... ruleIds) throws Exception {
        for (long ruleId : ruleIds) {
            RuleEvent event = RuleEvent.builder().id(ruleId).context(context).build();
            ruleProducer.onData(RuleEvent.toCharBuffer(event));
            LockSupport.parkNanos(1);
        }
    }

    // feed rules as batch
    private void runRuleBatch(long... ruleIds) throws Exception {
        CharBuffer[] rules = new CharBuffer[ruleIds.length];
        int i = 0;
        for (long ruleId : ruleIds) {
            RuleEvent event = RuleEvent.builder().id(ruleId).context(context).build();
            rules[i++] = RuleEvent.toCharBuffer(event);
            LockSupport.parkNanos(1);
        }
        ruleProducer.onData(rules); // feed rules as batch
    }

}
