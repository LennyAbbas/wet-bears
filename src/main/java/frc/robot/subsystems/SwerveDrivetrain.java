/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.trajectory.constraint.SwerveDriveKinematicsConstraint;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.AbsoluteEncoder;
import frc.robot.Utils;
import frc.robot.Constants.SwervePorts;
import frc.robot.Constants.SwerveConstants;

public class SwerveDrivetrain extends SubsystemBase {

        private CANSparkMax m_frontLeftDriveMotor;
        private CANSparkMax m_frontRightDriveMotor;
        private CANSparkMax m_backLeftDriveMotor;
        private CANSparkMax m_backRightDriveMotor;

        private CANSparkMax m_frontLeftTurningMotor;
        private CANSparkMax m_frontRightTurningMotor;
        private CANSparkMax m_backLeftTurningMotor;
        private CANSparkMax m_backRightTurningMotor;

        private SwerveWheel m_frontLeftSwerveWheel;
        private SwerveWheel m_frontRightSwerveWheel;
        private SwerveWheel m_backLeftSwerveWheel;
        private SwerveWheel m_backRightSwerveWheel;

        private AbsoluteEncoder m_frontLeftTurningEncoder;
        private AbsoluteEncoder m_frontRightTurningEncoder;
        private AbsoluteEncoder m_backLeftTurningEncoder;
        private AbsoluteEncoder m_backRightTurningEncoder;

        private double m_xSpeed;
        private double m_ySpeed;
        private double m_rotSpeed;
        private boolean m_isFieldRelative;

        private SwerveDriveKinematics m_kinematics;

        private AHRS m_gyro;
        private boolean m_isTurning; // dont want it to reset each time a method is called, want to save it

        // need pid to save headings/dynamic controls
        private PIDController m_pidController;

        /**
         * Creates a new SwerveDrivetrain.
         */
        public SwerveDrivetrain() {
                m_frontLeftDriveMotor = new CANSparkMax(SwervePorts.FRONT_LEFT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
                m_frontRightDriveMotor = new CANSparkMax(SwervePorts.FRONT_RIGHT_DRIVE_MOTOR_PORT,
                                MotorType.kBrushless);
                m_backLeftDriveMotor = new CANSparkMax(SwervePorts.BACK_LEFT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
                m_backRightDriveMotor = new CANSparkMax(SwervePorts.BACK_RIGHT_DRIVE_MOTOR_PORT, MotorType.kBrushless);

                m_frontLeftTurningMotor = new CANSparkMax(SwervePorts.FRONT_LEFT_TURNING_MOTOR_PORT,
                                MotorType.kBrushless);
                m_frontRightTurningMotor = new CANSparkMax(SwervePorts.FRONT_RIGHT_TURNING_MOTOR_PORT,
                                MotorType.kBrushless);
                m_backLeftTurningMotor = new CANSparkMax(SwervePorts.BACK_LEFT_TURNING_MOTOR_PORT,
                                MotorType.kBrushless);
                m_backRightTurningMotor = new CANSparkMax(SwervePorts.BACK_RIGHT_TURNING_MOTOR_PORT,
                                MotorType.kBrushless);

                m_frontLeftDriveMotor.setInverted(true);
                m_backLeftDriveMotor.setInverted(true);
                m_frontRightDriveMotor.setInverted(false);
                m_backRightDriveMotor.setInverted(false);

                m_frontLeftTurningEncoder = new AbsoluteEncoder(SwervePorts.FRONT_LEFT_TURNING_ENCODER_PORT, true,
                                SwerveRotationOffset.FRONT_LEFT_SWERVE_ROTATION_OFFSET);
                m_frontRightTurningEncoder = new AbsoluteEncoder(SwervePorts.FRONT_RIGHT_TURNING_ENCODER_PORT, true,
                                SwerveRotationOffset.FRONT_RIGHT_SWERVE_ROTATION_OFFSET);
                m_backLeftTurningEncoder = new AbsoluteEncoder(SwervePorts.BACK_RIGHT_TURNING_ENCODER_PORT, true,
                                SwerveRotationOffset.BACK_LEFT_SWERVE_ROTATION_OFFSET);
                m_backRightTurningEncoder = new AbsoluteEncoder(SwervePorts.BACK_LEFT_TURNING_ENCODER_PORT, true,
                                SwerveRotationOffset.BACK_RIGHT_SWERVE_ROTATION_OFFSET);

                m_frontLeftSwerveWheel = new SwerveWheel(m_frontLeftDriveMotor, m_frontLeftTurningMotor,
                                -SWERVE_X_DELTA, SWERVE_Y, m_frontLeftTurningEncoder);
                m_backLeftSwerveWheel = new SwerveWheel(m_backLeftDriveMotor, m_backLeftTurningMotor, SwerveConstraints.-SWERVE_X_DELTA,
                                -SWERVE_Y, m_backLeftTurningEncoder);
                m_frontRightSwerveWheel = new SwerveWheel(m_frontRightDriveMotor, m_frontRightTurningMotor, SWERVE_X,
                                SWERVE_Y, m_frontRightTurningEncoder);
                m_backRightSwerveWheel = new SwerveWheel(m_backRightDriveMotor, m_backRightTurningMotor, SWERVE_X,
                                -SWERVE_Y, m_backRightTurningEncoder);

                m_kinematics = new SwerveDriveKinematics(m_frontLeftSwerveWheel.getLocation(),
                                m_frontRightSwerveWheel.getLocation(), m_backLeftSwerveWheel.getLocation(),
                                m_backRightSwerveWheel.getLocation());

                m_gyro = new AHRS();

                m_pidController = new PIDController(Math.toRadians((MAX_METERS_PER_SECOND / 180) * 5), 0, 0); // needs
                                                                                                              // import
                m_pidController.enableContinuousInput(0, Math.PI * 2);
                m_pidController.setTolerance(1 / 36); // if off by a lil bit, then dont do anything (is in radians)
        }

        public void move(double xSpeed, double ySpeed, double rotSpeed, boolean isFieldRelative) {
                m_xSpeed = xSpeed;
                m_ySpeed = ySpeed;
                m_rotSpeed = rotSpeed;
                m_isFieldRelative = isFieldRelative;
        }

        @Override
        public void periodic() {
                ChassisSpeeds desiredSpeed = new ChassisSpeeds(m_xSpeed, m_ySpeed, m_rotSpeed);
                SwerveModuleState[] desiredSwerveModuleStates = m_kinematics.toSwerveModuleStates(desiredSpeed);
                SwerveDriveKinematics.normalizeWheelSpeeds(desiredSwerveModuleStates, MAX_METERS_PER_SECOND);
                m_frontLeftSwerveWheel.setState(desiredSwerveModuleStates[0]);
                m_frontRightSwerveWheel.setState(desiredSwerveModuleStates[1]);
                m_backLeftSwerveWheel.setState(desiredSwerveModuleStates[2]);
                m_backRightSwerveWheel.setState(desiredSwerveModuleStates[3]);

        }
}
