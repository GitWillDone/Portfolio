using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Enrolled
    {
        public string Grade { get; set; }
        public int StudentUId { get; set; }
        public string CId { get; set; }

        public virtual Classes C { get; set; }
        public virtual Students StudentU { get; set; }
    }
}
