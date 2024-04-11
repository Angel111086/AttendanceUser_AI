package ai.attendance.model;

public class SignUpModel {

    String BankName,BankAccountNumber,BranchName,IFSCCode,BloodGroup,CompanyName,CompanyAddress,CreationDate,
    Designation,DOB,Email,EmpCode,Experience,
    FatherName,Gender,JoiningDate,LocalAddress,Mobile, ModificationDate,
        MothersName,Notice_Period,Passport,Password,PermanentAddress,Photo,
    Salary,Status,Username,GurdianNumber;


    public SignUpModel(String bankName, String bankAccountNumber, String branchName, String IFSCCode, String bloodGroup, String companyName, String companyAddress, String creationDate, String designation, String DOB, String email, String empCode, String experience, String fatherName, String gender, String joiningDate, String localAddress, String mobile, String modificationDate, String mothersName, String notice_Period, String passport, String password, String permanentAddress, String photo, String salary, String status, String username, String gurdianNumber) {
        BankName = bankName;
        BankAccountNumber = bankAccountNumber;
        BranchName = branchName;
        this.IFSCCode = IFSCCode;
        BloodGroup = bloodGroup;
        CompanyName = companyName;
        CompanyAddress = companyAddress;
        CreationDate = creationDate;
        Designation = designation;
        this.DOB = DOB;
        Email = email;
        EmpCode = empCode;
        Experience = experience;
        FatherName = fatherName;
        Gender = gender;
        JoiningDate = joiningDate;
        LocalAddress = localAddress;
        Mobile = mobile;
        ModificationDate = modificationDate;
        MothersName = mothersName;
        Notice_Period = notice_Period;
        Passport = passport;
        Password = password;
        PermanentAddress = permanentAddress;
        Photo = photo;
        Salary = salary;
        Status = status;
        Username = username;
        GurdianNumber = gurdianNumber;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getBankAccountNumber() {
        return BankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        BankAccountNumber = bankAccountNumber;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getEmpCode() {
        return EmpCode;
    }

    public void setEmpCode(String empCode) {
        EmpCode = empCode;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String fatherName) {
        FatherName = fatherName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getJoiningDate() {
        return JoiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        JoiningDate = joiningDate;
    }

    public String getLocalAddress() {
        return LocalAddress;
    }

    public void setLocalAddress(String localAddress) {
        LocalAddress = localAddress;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getModificationDate() {
        return ModificationDate;
    }

    public void setModificationDate(String modificationDate) {
        ModificationDate = modificationDate;
    }

    public String getMothersName() {
        return MothersName;
    }

    public void setMothersName(String mothersName) {
        MothersName = mothersName;
    }

    public String getNotice_Period() {
        return Notice_Period;
    }

    public void setNotice_Period(String notice_Period) {
        Notice_Period = notice_Period;
    }

    public String getPassport() {
        return Passport;
    }

    public void setPassport(String passport) {
        Passport = passport;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPermanentAddress() {
        return PermanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        PermanentAddress = permanentAddress;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getSalary() {
        return Salary;
    }

    public void setSalary(String salary) {
        Salary = salary;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getGurdianNumber() {
        return GurdianNumber;
    }

    public void setGurdianNumber(String gurdianNumber) {
        GurdianNumber = gurdianNumber;
    }
}
