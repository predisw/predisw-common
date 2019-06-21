package com.predisw.common.util;

import com.predisw.common.util.ModelUtil.Result;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ModelUtilTest {

    @Test
    public void testWithReportParams(){

        AccessMonitoringEventReportParams param1 = new AccessMonitoringEventReportParams();
        AccessMonitoringEventReportParams param2 = new AccessMonitoringEventReportParams();

        AccessMonitoringEventReport report1 = new AccessMonitoringEventReport();
        AccessMonitoringEventReport report2 = new AccessMonitoringEventReport();

        report1.setActiveTime("2018");

        report1.setEventHandling(1);
        report2.setEventHandling(2);

        List<AccessMonitoringEventReport> list1 = new ArrayList<>(1);
        List<AccessMonitoringEventReport> list2 = new ArrayList<>(1);
        list1.add(report1);
        list2.add(report2);

        param1.setMonitoringEventReports(list1);
        param2.setMonitoringEventReports(list2);

        Result result = ModelUtil.isEqualByBasicFieldValue(param1, param2,new String[] {"activeTime"});
        System.out.println(result.isEqual());
        System.out.println(result.getErrorMsg());
    }


    @Test
    public void testWithEventReport(){
        AccessMonitoringEventReport report1 = new AccessMonitoringEventReport();
        AccessMonitoringEventReport report2 = new AccessMonitoringEventReport();

        report1.setEventHandling(1);
        report2.setEventHandling(2);

        Result result = ModelUtil.isEqualByBasicFieldValue(report1, report2,new String[] {"activeTime"});

        System.out.println(result.isEqual());
        System.out.println(result.getErrorMsg());



    }





    public static class AccessMonitoringEventReportParams {
        private String ttri;
        private String tltri;
        List<AccessMonitoringEventReport> monitoringEventReports;

        public String getTtri() {
            return ttri;
        }

        public void setTtri(String ttri) {
            this.ttri = ttri;
        }

        public String getTltri() {
            return tltri;
        }

        public void setTltri(String tltri) {
            this.tltri = tltri;
        }

        public List<AccessMonitoringEventReport> getMonitoringEventReports() {
            return monitoringEventReports;
        }

        public void setMonitoringEventReports(List<AccessMonitoringEventReport> monitoringEventReports) {
            this.monitoringEventReports = monitoringEventReports;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AccessMonitoringEventReportParams)) return false;

            AccessMonitoringEventReportParams that = (AccessMonitoringEventReportParams) o;

            if (getTtri() != null ? !getTtri().equals(that.getTtri()) : that.getTtri() != null) return false;
            if (getTltri() != null ? !getTltri().equals(that.getTltri()) : that.getTltri() != null) return false;
            return getMonitoringEventReports() != null ? getMonitoringEventReports().equals(that.getMonitoringEventReports()) : that.getMonitoringEventReports() == null;
        }

        @Override
        public int hashCode() {
            int result = getTtri() != null ? getTtri().hashCode() : 0;
            result = 31 * result + (getTltri() != null ? getTltri().hashCode() : 0);
            result = 31 * result + (getMonitoringEventReports() != null ? getMonitoringEventReports().hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "AccessMonitoringEventReportParams{" +
                    "ttri='" + ttri + '\'' +
                    ", tltri='" + tltri + '\'' +
                    ", monitoringEventReports=" + monitoringEventReports +
                    '}';
        }
    }

    public static class AccessMonitoringEventReport {
        private String externalId;
        private String msisdn;
        private Integer monitorType;
        private Integer reachabilityInformation;
        private Double latitude;
        private Double longitude;
        private Integer roamingStatus;
        private String activeTime;
        private Integer eventHandling;

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public Integer getMonitorType() {
            return monitorType;
        }

        public void setMonitorType(Integer monitorType) {
            this.monitorType = monitorType;
        }

        public Integer getReachabilityInformation() {
            return reachabilityInformation;
        }

        public void setReachabilityInformation(Integer reachabilityInformation) {
            this.reachabilityInformation = reachabilityInformation;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Integer getRoamingStatus() {
            return roamingStatus;
        }

        public void setRoamingStatus(Integer roamingStatus) {
            this.roamingStatus = roamingStatus;
        }

        public String getActiveTime() {
            return activeTime;
        }

        public void setActiveTime(String activeTime) {
            this.activeTime = activeTime;
        }

        public Integer getEventHandling() {
            return eventHandling;
        }

        public void setEventHandling(Integer eventHandling) {
            this.eventHandling = eventHandling;
        }

        @Override
        public String toString() {
            return "AccessMonitoringEventReport{" +
                    "externalId='" + externalId + '\'' +
                    ", msisdn='" + msisdn + '\'' +
                    ", monitorType=" + monitorType +
                    ", reachabilityInformation=" + reachabilityInformation +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", roamingStatus=" + roamingStatus +
                    ", activeTime='" + activeTime + '\'' +
                    ", eventHandling=" + eventHandling +
                    '}';
        }
    }


}
