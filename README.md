## Photo-Sorter
Sometimes you have a lot of photos from various devices, and their names might look like **_MG_5529.JPG** or **DSC00758.JPG**.

The Photo-Sorter app can solve this problem by renaming your files to a more readable format:  
**2007-Sep-10_13-55-40.jpeg**.

<img width="882" alt="Photo-Sorter" src="https://github.com/user-attachments/assets/17fb1072-c7a9-4788-9218-34cf2ac6e76b">

### Modes
- **Copy** - Copies renamed files to the destination directory
- **Move** - Moves renamed files to the destination directory
- **Replace** - Renames files directly in the source directory

**Move** and **Replace** modes remove files that are duplicated in the same directory.

### Supported file formats
- JPG/JPEG
- PNG

### How it defines the date
It uses the last modified time from the file metadata.

### How it defines duplicates
It compares files by their names, extensions, sizes, and MD5 hashes.  
Duplicates are removed **only** if they are placed in the same folder.
