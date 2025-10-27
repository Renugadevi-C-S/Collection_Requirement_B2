package com.example.DTOs;

public class RequestUpdate {


        private String department;
        private String justification;
        private String tanNo;
        private Integer noOfParticipants;
        private String curriculum;
        private String requestStatus;

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getJustification() {
            return justification;
        }

        public void setJustification(String justification) {
            this.justification = justification;
        }

        public String getTanNo() {
            return tanNo;
        }

        public void setTanNo(String tanNo) {
            this.tanNo = tanNo;
        }

        public Integer getNoOfParticipants() {
            return noOfParticipants;
        }

        public void setNoOfParticipants(Integer noOfParticipants) {
            this.noOfParticipants = noOfParticipants;
        }

        public String getCurriculum() {
            return curriculum;
        }

        public void setCurriculum(String curriculum) {
            this.curriculum = curriculum;
        }

        public String getRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(String requestStatus) {
            this.requestStatus = requestStatus;
        }


}
