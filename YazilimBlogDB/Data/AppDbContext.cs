using Microsoft.EntityFrameworkCore;

namespace YazilimBlog.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options)
            : base(options)
        { }

        public DbSet<Kullanici> Kullanicilar { get; set; }
        public DbSet<Blog> Bloglar { get; set; }
    }

    public class Kullanici
    {
        public int id { get; set; }
        public string kayit_yontem { get; set; }
        public string kullanici_adi { get; set; }
        public string? parola { get; set; }
        public string kayit_tarihi { get; set; }
        public string? mail { get; set; }
        public string? tel { get; set; }
    }

public class Blog
{
    public int id { get; set; }
    public int ekleyen_id { get; set; }
    public string metin { get; set; }
    public string tarih { get; set; }
    public string baslik { get; set; }
    public string? etiketler { get; set; }
}



}
