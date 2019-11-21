using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Assignments
    {
        public string Name { get; set; }
        public string Contents { get; set; }
        public DateTime? Due { get; set; }
        public string AId { get; set; }
        public uint? Points { get; set; }
        public string AcName { get; set; }
    }
}
