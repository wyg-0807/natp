var services = [
    {
        "method": "QueryMZPatientInfo",
        "description": "查询门诊患者",
        "paramTemplate": {
            "CardNo": ""
        }
    },
    {
        "method": "GetLisReportList",
        "description": "查询LIS患者",
        "paramTemplate": {
            "patientId": "患者ID",
            "type": "1=门诊，2=住院"
        }
    },
    {
        "method": "GetPacsReportList",
        "description": "查询PACS患者",
        "paramTemplate": {
            "patientId": "患者ID",
            "type": "1=门诊，2=住院"
        }
    }
];