## Photo-Sorter
Sometimes you have a lot of photos from various devices, and their names might look like **_MG_5529.JPG** or **DSC00758.JPG**.  
The Photo-Sorter renames your files to a more readable format: **2007-09-15_13-55-40.jpeg**.

### Modes
- **Copy** - Copies renamed files to the destination directory
- **Copy by years** - Copies renamed files to the folders by years
- **Move** - Moves renamed files to the destination directory
- **Move by years** - Moves renamed files to the folders by years
- **Replace** - Renames files directly in the source directory

**Move** and **Replace** modes remove files that are duplicated in the same directory.

### Supported file formats
- JPG/JPEG
- PNG  

**Unsupported format files are ignored**

### How it defines the date
It uses the created time or last modified time (takes the older one) from the file metadata.

### How it defines duplicates
It compares files by their names, extensions, sizes, and MD5 hashes.  
Duplicates are removed **only** if they are placed in the same folder.
