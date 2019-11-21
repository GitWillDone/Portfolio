using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Submission
    {
        public int StudentUId { get; set; }
        public string AId { get; set; }
        public uint? Score { get; set; }
        public string Contents { get; set; }
        public DateTime? Time { get; set; }

        public virtual Students StudentU { get; set; }
    }
}
