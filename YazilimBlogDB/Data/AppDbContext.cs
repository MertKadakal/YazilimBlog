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
        public DbSet<Yorum> Yorumlar { get; set; }
        public DbSet<Sikayet> Sikayetler { get; set; }
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
        public string? tarih { get; set; }
        public string baslik { get; set; }
        public string? etiketler { get; set; }
    }

    public class Yorum
    {
        public int id { get; set; }
        public int ekleyen_id { get; set; }
        public int eklenen_blog { get; set; }
        public string tarih { get; set; }
        public string icerik { get; set; }
        public byte puan { get; set; }
    }

    public class Sikayet
    {
        public int id { get; set; }
        public int eden_id { get; set; }
        public int? blog_id { get; set; }
        public int? yorum_id { get; set; }
        public int? hesap_id { get; set; }
        public string tarih { get; set; }
        public string aciklama { get; set; }
    }
}
