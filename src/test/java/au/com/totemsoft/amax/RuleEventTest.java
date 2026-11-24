package au.com.totemsoft.amax;

import java.nio.CharBuffer;
import java.time.LocalDate;
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
@Sql(scripts = "classpath:data.sql")
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

    private LocalDate latestDate() {
        return LocalDate.now().minusDays(1);
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
        context = new Context(latestDate());

        runRule(1L); // #SUM( #I1, #I2 )

        // wait until rule processing finish (or debug activities)
        System.in.read();
    }

    // feed rules one by one
    private void runRule(long... ruleIds) throws Exception {
        for (long ruleId : ruleIds) {
            RuleEvent event = new RuleEvent(ruleId, context);
            ruleProducer.onData(RuleEvent.toCharBuffer(event));
            LockSupport.parkNanos(1);
        }
    }

    // feed rules as batch
    private void runRuleBatch(long... ruleIds) throws Exception {
        CharBuffer[] rules = new CharBuffer[ruleIds.length];
        int i = 0;
        for (long ruleId : ruleIds) {
            RuleEvent event = new RuleEvent(ruleId, context);
            rules[i++] = RuleEvent.toCharBuffer(event);
            LockSupport.parkNanos(1);
        }
        ruleProducer.onData(rules); // feed rules as batch
    }

}
