using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace LMS.Models.LMSModels
{
    public partial class Team2LMSContext : DbContext
    {
        public Team2LMSContext()
        {
        }

        public Team2LMSContext(DbContextOptions<Team2LMSContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Administrators> Administrators { get; set; }
        public virtual DbSet<AssignmentCatagories> AssignmentCatagories { get; set; }
        public virtual DbSet<Assignments> Assignments { get; set; }
        public virtual DbSet<Classes> Classes { get; set; }
        public virtual DbSet<Courses> Courses { get; set; }
        public virtual DbSet<Departments> Departments { get; set; }
        public virtual DbSet<Enrolled> Enrolled { get; set; }
        public virtual DbSet<Professors> Professors { get; set; }
        public virtual DbSet<Students> Students { get; set; }
        public virtual DbSet<Submission> Submission { get; set; }
        public virtual DbSet<Users> Users { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. See http://go.microsoft.com/fwlink/?LinkId=723263 for guidance on storing connection strings.
                optionsBuilder.UseMySql("Server=atr.eng.utah.edu;User Id=u0545958;Password=MYsql1212;Database=Team2LMS");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Administrators>(entity =>
            {
                entity.HasKey(e => e.AdministratorUId)
                    .HasName("PRIMARY");

                entity.Property(e => e.AdministratorUId)
                    .HasColumnName("administrator_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.Dob)
                    .HasColumnName("DOB")
                    .HasColumnType("date");

                entity.Property(e => e.FName)
                    .HasColumnName("fName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.LName)
                    .HasColumnName("lName")
                    .HasColumnType("varchar(100)");

                entity.HasOne(d => d.AdministratorU)
                    .WithOne(p => p.Administrators)
                    .HasForeignKey<Administrators>(d => d.AdministratorUId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_adm_uid");
            });

            modelBuilder.Entity<AssignmentCatagories>(entity =>
            {
                entity.HasKey(e => new { e.Name, e.CId })
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.CId)
                    .HasName("fk_asc_dic_del");

                entity.HasIndex(e => new { e.Name, e.CId })
                    .HasName("uc_nam_cid")
                    .IsUnique();

                entity.Property(e => e.Name).HasColumnType("varchar(100)");

                entity.Property(e => e.CId)
                    .HasColumnName("cID")
                    .HasColumnType("varchar(100)");

                entity.HasOne(d => d.C)
                    .WithMany(p => p.AssignmentCatagories)
                    .HasForeignKey(d => d.CId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_asc_dic_del");
            });

            modelBuilder.Entity<Assignments>(entity =>
            {
                entity.HasKey(e => e.AId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.AcName)
                    .HasName("fk_asg_acN");

                entity.Property(e => e.AId)
                    .HasColumnName("aID")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.AcName)
                    .HasColumnName("acName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.Contents).HasColumnType("varchar(8192)");

                entity.Property(e => e.Due).HasColumnType("date");

                entity.Property(e => e.Name).HasColumnType("varchar(100)");
            });

            modelBuilder.Entity<Classes>(entity =>
            {
                entity.HasKey(e => e.CId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.CatalogId)
                    .HasName("CatalogID")
                    .IsUnique();

                entity.HasIndex(e => e.ProfessorUId)
                    .HasName("fk_cla_pid_del");

                entity.HasIndex(e => e.Semester)
                    .HasName("Semester")
                    .IsUnique();

                entity.HasIndex(e => e.Subject)
                    .HasName("fk_cla_sub_del");

                entity.HasIndex(e => new { e.Semester, e.CatalogId })
                    .HasName("uc_sem_catid")
                    .IsUnique();

                entity.Property(e => e.CId)
                    .HasColumnName("cID")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.CatalogId).HasColumnName("CatalogID");

                entity.Property(e => e.End).HasColumnType("date");

                entity.Property(e => e.Loc).HasColumnType("varchar(100)");

                entity.Property(e => e.ProfessorUId)
                    .HasColumnName("professor_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.Semester).HasColumnType("varchar(10)");

                entity.Property(e => e.Start).HasColumnType("date");

                entity.Property(e => e.Subject)
                    .IsRequired()
                    .HasColumnType("varchar(4)");

                entity.Property(e => e.Year).HasColumnType("year");

                entity.HasOne(d => d.Catalog)
                    .WithOne(p => p.Classes)
                    .HasForeignKey<Classes>(d => d.CatalogId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_cla_coid_upd");

                entity.HasOne(d => d.ProfessorU)
                    .WithMany(p => p.Classes)
                    .HasForeignKey(d => d.ProfessorUId)
                    .HasConstraintName("fk_cla_pid_del");

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Classes)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_cla_sub_del");
            });

            modelBuilder.Entity<Courses>(entity =>
            {
                entity.HasKey(e => e.CatalogId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject)
                    .HasName("fk_sub");

                entity.HasIndex(e => new { e.Num, e.Subject })
                    .HasName("uc_num_sub")
                    .IsUnique();

                entity.Property(e => e.CatalogId).HasColumnName("CatalogID");

                entity.Property(e => e.Name).HasColumnType("varchar(100)");

                entity.Property(e => e.Subject).HasColumnType("varchar(4)");

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Courses)
                    .HasForeignKey(d => d.Subject)
                    .HasConstraintName("fk_sub");
            });

            modelBuilder.Entity<Departments>(entity =>
            {
                entity.HasKey(e => e.Subject)
                    .HasName("PRIMARY");

                entity.Property(e => e.Subject).HasColumnType("varchar(4)");

                entity.Property(e => e.Name).HasColumnType("varchar(100)");
            });

            modelBuilder.Entity<Enrolled>(entity =>
            {
                entity.HasKey(e => new { e.StudentUId, e.CId })
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.CId)
                    .HasName("cID");

                entity.Property(e => e.StudentUId)
                    .HasColumnName("student_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.CId)
                    .HasColumnName("cID")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.Grade).HasColumnType("varchar(2)");

                entity.HasOne(d => d.C)
                    .WithMany(p => p.Enrolled)
                    .HasForeignKey(d => d.CId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Enrolled_ibfk_2");

                entity.HasOne(d => d.StudentU)
                    .WithMany(p => p.Enrolled)
                    .HasForeignKey(d => d.StudentUId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Enrolled_ibfk_1");
            });

            modelBuilder.Entity<Professors>(entity =>
            {
                entity.HasKey(e => e.ProfessorUId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject)
                    .HasName("fk_sub_del");

                entity.Property(e => e.ProfessorUId)
                    .HasColumnName("professor_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.Dob)
                    .HasColumnName("DOB")
                    .HasColumnType("date");

                entity.Property(e => e.FName)
                    .HasColumnName("fName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.Name).HasColumnType("varchar(100)");

                entity.Property(e => e.Subject)
                    .IsRequired()
                    .HasColumnType("varchar(4)");

                entity.HasOne(d => d.ProfessorU)
                    .WithOne(p => p.Professors)
                    .HasForeignKey<Professors>(d => d.ProfessorUId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Professors_ibfk_1");

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Professors)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Professors_ibfk_2");
            });

            modelBuilder.Entity<Students>(entity =>
            {
                entity.HasKey(e => e.StudentUId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject)
                    .HasName("fk_stu_del");

                entity.Property(e => e.StudentUId)
                    .HasColumnName("student_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.Dob)
                    .HasColumnName("DOB")
                    .HasColumnType("date");

                entity.Property(e => e.FName)
                    .HasColumnName("fName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.LName)
                    .HasColumnName("lName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.Subject)
                    .IsRequired()
                    .HasColumnType("varchar(4)");

                entity.HasOne(d => d.StudentU)
                    .WithOne(p => p.Students)
                    .HasForeignKey<Students>(d => d.StudentUId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Students_ibfk_1");

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Students)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Students_ibfk_2");
            });

            modelBuilder.Entity<Submission>(entity =>
            {
                entity.HasKey(e => new { e.StudentUId, e.AId })
                    .HasName("PRIMARY");

                entity.Property(e => e.StudentUId)
                    .HasColumnName("student_uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.AId)
                    .HasColumnName("aID")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.Contents).HasColumnType("varchar(8192)");

                entity.Property(e => e.Time).HasColumnType("date");

                entity.HasOne(d => d.StudentU)
                    .WithMany(p => p.Submission)
                    .HasForeignKey(d => d.StudentUId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Submission_ibfk_1");
            });

            modelBuilder.Entity<Users>(entity =>
            {
                entity.HasKey(e => e.UId)
                    .HasName("PRIMARY");

                entity.Property(e => e.UId)
                    .HasColumnName("uID")
                    .HasColumnType("int(8)");

                entity.Property(e => e.Dob)
                    .HasColumnName("DOB")
                    .HasColumnType("date");

                entity.Property(e => e.FName)
                    .HasColumnName("fName")
                    .HasColumnType("varchar(100)");

                entity.Property(e => e.LName)
                    .HasColumnName("lName")
                    .HasColumnType("varchar(100)");
            });
        }
    }
}
